package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import beans.Question;
import dao.MockExamDao;

@WebServlet("/MockExamResultRegistrationServlet")
public class MockExamResultRegistrationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int TOTAL_QUESTIONS = 100;
    private static final long TIME_LIMIT_SECONDS = 75 * 60;

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        HttpSession session = request.getSession();
        List<Question> questions = getActiveQuestions(session);
        Timestamp startTime = (Timestamp) session.getAttribute(
                "mockExamStartTime"
        );
        if (questions == null || startTime == null) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "進行中の模試がありません"
            );
            return;
        }
        if (questions.size() != TOTAL_QUESTIONS) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "模試問題数が正しくありません"
            );
            return;
        }

        Timestamp endTime = new Timestamp(System.currentTimeMillis());
        long elapsedSeconds = Math.max(
                0,
                (endTime.getTime() - startTime.getTime()) / 1000
        );
        int answeredCount = 0;
        int correctCount = 0;

        if (elapsedSeconds <= TIME_LIMIT_SECONDS + 30) {
            for (Question question : questions) {
                String answer = request.getParameter(
                        "answer_" + question.getQuestionId()
                );
                if (answer == null || !answer.matches("[A-Da-d]")) {
                    continue;
                }
                answeredCount++;
                if (answer.equalsIgnoreCase(
                        question.getCorrectAnswer()
                )) {
                    correctCount++;
                }
            }
        }

        int incorrectCount = answeredCount - correctCount;
        int unansweredCount = TOTAL_QUESTIONS - answeredCount;
        try {
            long mockId = new MockExamDao().insertMockExamResult(
                    startTime,
                    endTime,
                    TOTAL_QUESTIONS,
                    answeredCount,
                    correctCount,
                    incorrectCount,
                    unansweredCount
            );
            session.removeAttribute("mockExamQuestions");
            session.removeAttribute("mockExamStartTime");
            response.sendRedirect(
                    request.getContextPath()
                            + "/MockExamResultsServlet?mockId=" + mockId
            );
        } catch (SQLException e) {
            throw new ServletException("模試結果を保存できませんでした", e);
        }
    }

    private List<Question> getActiveQuestions(HttpSession session) {
        Object value = session.getAttribute("mockExamQuestions");
        if (!(value instanceof List<?> list)) {
            return null;
        }

        java.util.ArrayList<Question> questions =
                new java.util.ArrayList<>();
        for (Object item : list) {
            if (!(item instanceof Question question)) {
                return null;
            }
            questions.add(question);
        }
        return questions;
    }
}
