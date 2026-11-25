package controller;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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

        if (partType < 5 || partType > 7) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Part 5～7を選択してください"
            );
            return;
        }

        String answer = request.getParameter("answer");

        if (answer == null
                || !answer.matches("[A-Da-d]")) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "回答はA～Dから選択してください"
            );
            return;
        }

        answer = answer.toUpperCase();

        try {
            Question question =
                    new QuestionDao().getQuestionById(questionId);

            if (question == null
                    || question.getPartType() != partType) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "対象の練習問題が見つかりません"
                );
                return;
            }

            boolean correct =
                    answer.equalsIgnoreCase(
                            question.getCorrectAnswer()
                    );

            new LearningHistoryDao().insertHistory(
                    questionId,
                    correct,
                    answer
            );

            HttpSession session = request.getSession();

            String answeredName =
                    "practiceAnsweredIds_part_" + partType;

            synchronized (session) {
                Object value =
                        session.getAttribute(answeredName);

                Set<Long> answeredIds;

                if (value instanceof Set<?> set) {
                    answeredIds = new HashSet<>();

                    for (Object item : set) {
                        if (item instanceof Long id) {
                            answeredIds.add(id);
                        }
                    }
                } else {
                    answeredIds = new HashSet<>();
                }

                answeredIds.add(questionId);

                session.setAttribute(
                        answeredName,
                        answeredIds
                );

                session.setAttribute(
                        "practiceFeedback",
                        new PracticeFeedback(
                                questionId,
                                answer,
                                correct,
                                question.getCorrectAnswer(),
                                question.getExplanation()
                        )
                );
            }

            response.sendRedirect(
                    request.getContextPath()
                            + "/PracticeServlet?partType="
                            + partType
            );

        } catch (SQLException e) {
            throw new ServletException(
                    "練習結果を保存できませんでした",
                    e
            );
        }
    }

    public static class PracticeFeedback implements Serializable {
        private static final long serialVersionUID = 1L;

        private final long questionId;
        private final String answer;
        private final boolean correct;
        private final String correctAnswer;
        private final String explanation;

        public PracticeFeedback(
                long questionId,
                String answer,
                boolean correct,
                String correctAnswer,
                String explanation
        ) {
            this.questionId = questionId;
            this.answer = answer;
            this.correct = correct;
            this.correctAnswer = correctAnswer;
            this.explanation = explanation;
        }

        public long getQuestionId() {
            return questionId;
        }

        public String getAnswer() {
            return answer;
        }

        public boolean isCorrect() {
            return correct;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public String getExplanation() {
            return explanation;
        }
    }
}