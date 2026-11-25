package controller;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import beans.Question;
import dao.LearningHistoryDao;
import dao.QuestionDao;

@WebServlet("/PracticeResultRegistrationServlet")
public class PracticeResultRegistrationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        long questionId;
        int partType;
        try {
            questionId = Long.parseLong(
                    request.getParameter("questionId")
            );
            partType = Integer.parseInt(
                    request.getParameter("partType")
            );
        } catch (NumberFormatException | NullPointerException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "問題IDまたはパート番号が正しくありません"
            );
            return;
        }

        String answer = request.getParameter("answer");
        if (answer == null || !answer.matches("[A-Da-d]")) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "回答はA～Dから選択してください"
            );
            return;
        }

        try {
            Question question = new QuestionDao().getQuestionById(questionId);
            if (question == null || question.getPartType() != partType) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "対象の練習問題が見つかりません"
                );
                return;
            }

            boolean correct = answer.equalsIgnoreCase(
                    question.getCorrectAnswer()
            );
            long historyId = new LearningHistoryDao().insertHistory(
                    questionId,
                    correct,
                    answer.toUpperCase()
            );
            response.sendRedirect(
                    request.getContextPath()
                            + "/PracticeResultsServlet?historyId="
                            + historyId
            );
        } catch (SQLException e) {
            throw new ServletException("練習結果を保存できませんでした", e);
        }
    }
}
