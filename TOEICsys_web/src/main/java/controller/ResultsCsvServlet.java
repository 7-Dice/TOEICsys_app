package controller;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import beans.LearningHistoryRecord;
import beans.MockExamResult;
import dao.LearningHistoryDao;
import dao.MockExamDao;

@WebServlet("/ResultsCsvServlet")
public class ResultsCsvServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        String type = request.getParameter("type");
        if ("practice".equals(type)) {
            exportPracticeResults(response);
        } else if ("mock".equals(type)) {
            exportMockExamResults(response);
        } else {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "CSVの種類はpracticeまたはmockを指定してください"
            );
        }
    }

    private void exportPracticeResults(HttpServletResponse response)
            throws ServletException, IOException {
        List<LearningHistoryRecord> records;
        try {
            records = new LearningHistoryDao().findAll();
        } catch (SQLException e) {
            throw new ServletException(
                    "練習結果CSVを取得できませんでした",
                    e
            );
        }

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"practice-results.csv\""
        );
        try (PrintWriter writer = csvWriter(response)) {
            writeRow(
                    writer,
                    "History ID",
                    "Answered At",
                    "Part",
                    "Question ID",
                    "Question",
                    "Your Answer",
                    "Correct Answer",
                    "Result",
                    "Explanation"
            );
            for (LearningHistoryRecord record : records) {
                writeRow(
                        writer,
                        Long.toString(record.getHistoryId()),
                        record.getAnsweredAt().toString(),
                        Integer.toString(record.getPartType()),
                        Long.toString(record.getQuestionId()),
                        record.getQuestionText(),
                        record.getUserAnswer(),
                        record.getCorrectAnswer(),
                        record.getCorrect() == null
                                ? ""
                                : record.getCorrect() ? "Correct" : "Incorrect",
                        record.getExplanation()
                );
            }
        }
    }

    private void exportMockExamResults(HttpServletResponse response)
            throws ServletException, IOException {
        List<MockExamResult> results;
        try {
            results = new MockExamDao().findAll();
        } catch (SQLException e) {
            throw new ServletException(
                    "模試結果CSVを取得できませんでした",
                    e
            );
        }

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"mock-exam-results.csv\""
        );
        try (PrintWriter writer = csvWriter(response)) {
            writeRow(
                    writer,
                    "Mock ID",
                    "Start Time",
                    "End Time",
                    "Total Questions",
                    "Answered",
                    "Correct",
                    "Incorrect",
                    "Unanswered",
                    "Correct Percent",
                    "Elapsed Seconds"
            );
            for (MockExamResult result : results) {
                writeRow(
                        writer,
                        Long.toString(result.getMockId()),
                        result.getStartTime().toString(),
                        result.getEndTime().toString(),
                        Integer.toString(result.getTotalQuestions()),
                        Integer.toString(result.getAnsweredCount()),
                        Integer.toString(result.getCorrectCount()),
                        Integer.toString(result.getIncorrectCount()),
                        Integer.toString(result.getUnansweredCount()),
                        Integer.toString(result.getScorePercent()),
                        Long.toString(result.getElapsedSeconds())
                );
            }
        }
    }

    private PrintWriter csvWriter(HttpServletResponse response)
            throws IOException {
        jakarta.servlet.ServletOutputStream output =
                response.getOutputStream();
        output.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
        return new PrintWriter(
                new OutputStreamWriter(
                        output,
                        StandardCharsets.UTF_8
                )
        );
    }

    private void writeRow(PrintWriter writer, String... values) {
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                writer.print(',');
            }
            String value = values[i] == null ? "" : values[i];
            if (!value.isEmpty()
                    && "=+-@".indexOf(value.charAt(0)) >= 0) {
                value = "'" + value;
            }
            writer.print('"');
            writer.print(value.replace("\"", "\"\""));
            writer.print('"');
        }
        writer.println();
    }
}
