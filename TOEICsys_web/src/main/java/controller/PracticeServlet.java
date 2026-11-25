package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import beans.PassageQuestionGroup;
import beans.Question;
import dao.PassageGroupDao;
import dao.QuestionDao;

@WebServlet("/PracticeServlet")
public class PracticeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String partParameter = request.getParameter("partType");
        if (partParameter == null || partParameter.isBlank()) {
            request.getRequestDispatcher("/WEB-INF/practice.jsp")
                    .forward(request, response);
            return;
        }

        int partType;
        try {
            partType = Integer.parseInt(partParameter);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "パート番号が正しくありません");
            request.getRequestDispatcher("/WEB-INF/practice.jsp")
                    .forward(request, response);
            return;
        }

        if (partType < 5 || partType > 7) {
            request.setAttribute("errorMessage", "Part 5～7を選択してください");
            request.getRequestDispatcher("/WEB-INF/practice.jsp")
                    .forward(request, response);
            return;
        }

        try {
            List<Question> questions =
                    new QuestionDao().getQuestionsForPractice(partType, true);
            if (questions.isEmpty()) {
                request.setAttribute("errorMessage", "問題データがありません");
                request.getRequestDispatcher("/WEB-INF/practice.jsp")
                        .forward(request, response);
                return;
            }

            List<Question> standaloneQuestions = new ArrayList<>();
            for (Question question : questions) {
                if (question.getGroupId() == null) {
                    standaloneQuestions.add(question);
                }
            }

            List<PassageQuestionGroup> passageGroups =
                    partType == 5
                            ? List.of()
                            : PassageQuestionGrouper.group(
                                    questions,
                                    partType,
                                    new PassageGroupDao()
                            );

            request.setAttribute("partType", partType);
            request.setAttribute("questionList", questions);
            request.setAttribute(
                    "standaloneQuestions",
                    standaloneQuestions
            );
            request.setAttribute("passageGroups", passageGroups);
            request.getRequestDispatcher("/WEB-INF/practice.jsp")
                    .forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("練習問題を取得できませんでした", e);
        }
    }
}