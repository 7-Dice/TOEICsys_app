package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import beans.MockExamResult;

public class MockExamDao {
    private static final String RESULT_SELECT = """
            SELECT mock_id,
                   start_time,
                   end_time,
                   total_questions,
                   answered_count,
                   correct_count,
                   incorrect_count,
                   unanswered_count
              FROM mock_exam_results
            """;

    public long insertMockExamResult(
            Timestamp startTime,
            Timestamp endTime,
            int totalQuestions,
            int answeredCount,
            int correctCount,
            int incorrectCount,
            int unansweredCount
    ) throws SQLException {
        String sql = """
                INSERT INTO mock_exam_results(
                    start_time,
                    end_time,
                    total_questions,
                    answered_count,
                    correct_count,
                    incorrect_count,
                    unanswered_count
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {
            ps.setTimestamp(1, startTime);
            ps.setTimestamp(2, endTime);
            ps.setInt(3, totalQuestions);
            ps.setInt(4, answeredCount);
            ps.setInt(5, correctCount);
            ps.setInt(6, incorrectCount);
            ps.setInt(7, unansweredCount);

            if (ps.executeUpdate() != 1) {
                throw new SQLException("模試結果を登録できませんでした");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("模試結果IDを取得できませんでした");
                }
                return keys.getLong(1);
            }
        }
    }

    public MockExamResult findById(long mockId) throws SQLException {
        List<MockExamResult> results = query(
                " WHERE mock_id = ? ",
                mockId,
                null
        );
        return results.isEmpty() ? null : results.get(0);
    }

    public List<MockExamResult> findRecent(int limit) throws SQLException {
        return query("", null, limit);
    }

    public List<MockExamResult> findAll() throws SQLException {
        return query("", null, null);
    }

    private List<MockExamResult> query(
            String whereClause,
            Long mockId,
            Integer limit
    ) throws SQLException {
        StringBuilder sql = new StringBuilder(RESULT_SELECT)
                .append(whereClause)
                .append(" ORDER BY mock_id DESC ");
        if (limit != null) {
            sql.append(" LIMIT ? ");
        }

        List<MockExamResult> results = new ArrayList<>();
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int parameterIndex = 1;
            if (mockId != null) {
                ps.setLong(parameterIndex++, mockId);
            }
            if (limit != null) {
                ps.setInt(parameterIndex, limit);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new MockExamResult(
                            rs.getLong("mock_id"),
                            rs.getTimestamp("start_time"),
                            rs.getTimestamp("end_time"),
                            rs.getInt("total_questions"),
                            rs.getInt("answered_count"),
                            rs.getInt("correct_count"),
                            rs.getInt("incorrect_count"),
                            rs.getInt("unanswered_count")
                    ));
                }
            }
        }
        return results;
    }
}
