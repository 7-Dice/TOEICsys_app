package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import beans.Question;

public class QuestionDao {

    public List<Question> getRandomMockExamQuestions() throws SQLException {
        List<Question> questions = new ArrayList<>();

        try (Connection conn = DBManager.getConnection()) {
            addRandomQuestions(conn, questions, 5, null, 30);
            addRandomQuestions(conn, questions, 6, null, 16);
            addRandomQuestions(conn, questions, 7, "SINGLE", 29);
            addRandomQuestions(conn, questions, 7, "DOUBLE", 10);
            addRandomQuestions(conn, questions, 7, "TRIPLE", 15);
        }

        if (questions.size() != 100) {
            throw new SQLException(
                    "模試に必要な100問を取得できませんでした: "
                            + questions.size() + "問"
            );
        }

        questions.sort(
                Comparator.comparingInt(Question::getPartType)
                        .thenComparing(
                                Question::getGroupId,
                                Comparator.nullsFirst(
                                        Comparator.naturalOrder()
                                )
                        )
                        .thenComparing(
                                Question::getSequenceNo,
                                Comparator.nullsFirst(
                                        Comparator.naturalOrder()
                                )
                        )
                        .thenComparingLong(Question::getQuestionId)
        );
        return questions;
    }

    private void addRandomQuestions(
            Connection conn,
            List<Question> questions,
            int partType,
            String groupType,
            int limit
    ) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT q.question_id,
                       q.group_id,
                       q.sequence_no,
                       q.part_type,
                       q.question_text,
                       q.option_a,
                       q.option_b,
                       q.option_c,
                       q.option_d,
                       q.correct_answer,
                       q.explanation
                  FROM questions q
                  LEFT JOIN passage_groups g ON g.group_id = q.group_id
                 WHERE q.part_type = ?
                """);
        if (groupType != null) {
            sql.append(" AND g.group_type = ? ");
        }
        sql.append(" ORDER BY RAND() LIMIT ? ");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int parameterIndex = 1;
            ps.setInt(parameterIndex++, partType);
            if (groupType != null) {
                ps.setString(parameterIndex++, groupType);
            }
            ps.setInt(parameterIndex, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long groupId = rs.getObject("group_id") == null
                            ? null
                            : rs.getLong("group_id");
                    Integer sequenceNo = rs.getObject("sequence_no") == null
                            ? null
                            : rs.getInt("sequence_no");

                    questions.add(new Question(
                            rs.getLong("question_id"),
                            groupId,
                            sequenceNo,
                            rs.getInt("part_type"),
                            rs.getString("question_text"),
                            rs.getString("option_a"),
                            rs.getString("option_b"),
                            rs.getString("option_c"),
                            rs.getString("option_d"),
                            rs.getString("correct_answer"),
                            rs.getString("explanation")
                    ));
                }
            }
        }
    }

    public List<Question> getQuestionByPart(int partType) {
        List<Question> list = new ArrayList<>();

        String sql = """
                SELECT question_id,
                       group_id,
                       sequence_no,
                       part_type,
                       question_text,
                       option_a,
                       option_b,
                       option_c,
                       option_d,
                       correct_answer,
                       explanation
                  FROM questions
                 WHERE part_type = ?
              ORDER BY group_id, sequence_no, question_id
                """;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, partType);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long groupId = rs.getObject("group_id") == null
                            ? null
                            : rs.getLong("group_id");

                    Integer sequenceNo = rs.getObject("sequence_no") == null
                            ? null
                            : rs.getInt("sequence_no");

                    list.add(new Question(
                            rs.getLong("question_id"),
                            groupId,
                            sequenceNo,
                            rs.getInt("part_type"),
                            rs.getString("question_text"),
                            rs.getString("option_a"),
                            rs.getString("option_b"),
                            rs.getString("option_c"),
                            rs.getString("option_d"),
                            rs.getString("correct_answer"),
                            rs.getString("explanation")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public Question getQuestionById(long questionId) throws SQLException {
        String sql = """
                SELECT question_id,
                       group_id,
                       sequence_no,
                       part_type,
                       question_text,
                       option_a,
                       option_b,
                       option_c,
                       option_d,
                       correct_answer,
                       explanation
                  FROM questions
                 WHERE question_id = ?
                """;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, questionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Long groupId = rs.getObject("group_id") == null
                            ? null
                            : rs.getLong("group_id");

                    Integer sequenceNo = rs.getObject("sequence_no") == null
                            ? null
                            : rs.getInt("sequence_no");

                    return new Question(
                            rs.getLong("question_id"),
                            groupId,
                            sequenceNo,
                            rs.getInt("part_type"),
                            rs.getString("question_text"),
                            rs.getString("option_a"),
                            rs.getString("option_b"),
                            rs.getString("option_c"),
                            rs.getString("option_d"),
                            rs.getString("correct_answer"),
                            rs.getString("explanation")
                    );
                }
            }

        }

        return null;
    }

    public List<Question> getQuestionsForPractice(
            int partType,
            boolean prioritizeIncorrect
    ) throws SQLException {
        List<Question> questionList = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("""
                SELECT q.question_id,
                       q.group_id,
                       q.sequence_no,
                       q.part_type,
                       q.question_text,
                       q.option_a,
                       q.option_b,
                       q.option_c,
                       q.option_d,
                       q.correct_answer,
                       q.explanation
                  FROM questions q
                """);

        if (prioritizeIncorrect) {
            sql.append("""
                    LEFT JOIN (
                        SELECT history.question_id,
                               history.is_correct
                          FROM learning_history history
                          JOIN (
                                SELECT question_id,
                                       MAX(history_id) AS latest_history_id
                                  FROM learning_history
                                 GROUP BY question_id
                          ) latest
                            ON latest.question_id = history.question_id
                           AND latest.latest_history_id = history.history_id
                    ) h ON h.question_id = q.question_id
                    """);
        }

        if (partType > 0) {
            sql.append(" WHERE q.part_type = ? ");
        }

        if (prioritizeIncorrect) {
            sql.append("""
                    ORDER BY CASE WHEN h.is_correct = FALSE THEN 0 ELSE 1 END,
                             q.question_id
                    """);
        } else {
            sql.append(" ORDER BY q.question_id ");
        }

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql.toString())) {

            if (partType > 0) {
                ps.setInt(1, partType);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long groupId = rs.getObject("group_id") == null
                            ? null
                            : rs.getLong("group_id");

                    Integer sequenceNo = rs.getObject("sequence_no") == null
                            ? null
                            : rs.getInt("sequence_no");

                    questionList.add(new Question(
                            rs.getLong("question_id"),
                            groupId,
                            sequenceNo,
                            rs.getInt("part_type"),
                            rs.getString("question_text"),
                            rs.getString("option_a"),
                            rs.getString("option_b"),
                            rs.getString("option_c"),
                            rs.getString("option_d"),
                            rs.getString("correct_answer"),
                            rs.getString("explanation")
                    ));
                }
            }

        }

        return questionList;
    }

    public List<Question> getAllQuestions() {
        List<Question> questionList = new ArrayList<>();

        String sql = """
                SELECT question_id,
                       group_id,
                       sequence_no,
                       part_type,
                       question_text,
                       option_a,
                       option_b,
                       option_c,
                       option_d,
                       correct_answer,
                       explanation
                  FROM questions
              ORDER BY group_id, sequence_no, question_id
                """;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Long groupId = rs.getObject("group_id") == null
                        ? null
                        : rs.getLong("group_id");

                Integer sequenceNo = rs.getObject("sequence_no") == null
                        ? null
                        : rs.getInt("sequence_no");

                questionList.add(new Question(
                        rs.getLong("question_id"),
                        groupId,
                        sequenceNo,
                        rs.getInt("part_type"),
                        rs.getString("question_text"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("correct_answer"),
                        rs.getString("explanation")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questionList;
    }
}