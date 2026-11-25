package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import beans.LearningHistoryRecord;

public class LearningHistoryDao {
    private static final String HISTORY_SELECT = """
            SELECT h.history_id,
                   h.question_id,
                   q.part_type,
                   q.question_text,
                   h.user_answer,
                   q.correct_answer,
                   h.is_correct,
                   h.answered_at,
                   q.explanation
              FROM learning_history h
              JOIN questions q ON q.question_id = h.question_id
            """;

    public long insertHistory(
            long questionId,
            boolean isCorrect,
            String userAnswer
    ) throws SQLException {
        String sql = """
                INSERT INTO learning_history(
                    question_id,
                    user_answer,
                    is_correct
                )
                VALUES (?, ?, ?)
                """;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {
            ps.setLong(1, questionId);
            ps.setString(2, userAnswer);
            ps.setBoolean(3, isCorrect);

            if (ps.executeUpdate() != 1) {
                throw new SQLException("回答履歴を登録できませんでした");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException(
                            "回答履歴IDを取得できませんでした"
                    );
                }
                return keys.getLong(1);
            }
        }
    }

    public LearningHistoryRecord findById(long historyId)
            throws SQLException {
        List<LearningHistoryRecord> records = query(
                " WHERE h.history_id = ? ",
                historyId,
                null
        );
        return records.isEmpty() ? null : records.get(0);
    }

    public List<LearningHistoryRecord> findRecent(int limit)
            throws SQLException {
        return query("", null, limit);
    }

    public List<LearningHistoryRecord> findAll()
            throws SQLException {
        return query("", null, null);
    }

    private List<LearningHistoryRecord> query(
            String whereClause,
            Long historyId,
            Integer limit
    ) throws SQLException {
        StringBuilder sql = new StringBuilder(HISTORY_SELECT)
                .append(whereClause)
                .append(" ORDER BY h.history_id DESC ");
        if (limit != null) {
            sql.append(" LIMIT ? ");
        }

        List<LearningHistoryRecord> records = new ArrayList<>();
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int parameterIndex = 1;
            if (historyId != null) {
                ps.setLong(parameterIndex++, historyId);
            }
            if (limit != null) {
                ps.setInt(parameterIndex, limit);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    boolean correctValue = rs.getBoolean("is_correct");
                    Boolean correct = rs.wasNull() ? null : correctValue;
                    records.add(new LearningHistoryRecord(
                            rs.getLong("history_id"),
                            rs.getLong("question_id"),
                            rs.getInt("part_type"),
                            rs.getString("question_text"),
                            rs.getString("user_answer"),
                            rs.getString("correct_answer"),
                            correct,
                            rs.getTimestamp("answered_at"),
                            rs.getString("explanation")
                    ));
                }
            }
        }
        return records;
    }
}
