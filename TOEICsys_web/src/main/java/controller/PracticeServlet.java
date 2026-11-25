package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            sessionFeedbackClean(request);
            request.getRequestDispatcher("/WEB-INF/practice.jsp")
                    .forward(request, response);
            return;
        }
        
        String reset = request.getParameter("reset");

        if ("true".equals(reset)
                && partParameter != null
                && !partParameter.isBlank()) {
            int resetPartType = Integer.parseInt(partParameter);

            request.getSession().removeAttribute(
                    "practiceAnsweredIds_part_" + resetPartType
            );
            request.getSession().removeAttribute("practiceFeedback");
        }

        int partType;
        try {
            partType = Integer.parseInt(partParameter);
        } catch (NumberFormatException e) {
            request.setAttribute(
                    "errorMessage",
                    "パート番号が正しくありません"
            );
            request.getRequestDispatcher("/WEB-INF/practice.jsp")
                    .forward(request, response);
            return;
        }

        if (partType < 5 || partType > 7) {
            request.setAttribute(
                    "errorMessage",
                    "Part 5～7を選択してください"
            );
            request.getRequestDispatcher("/WEB-INF/practice.jsp")
                    .forward(request, response);
            return;
        }

        try {
            List<Question> allQuestions =
                    new QuestionDao()
                            .getQuestionsForPractice(partType, true);

            if (allQuestions.isEmpty()) {
                request.setAttribute(
                        "errorMessage",
                        "問題データがありません"
                );
                request.getRequestDispatcher("/WEB-INF/practice.jsp")
                        .forward(request, response);
                return;
            }

            Set<Long> answeredIds =
                    getAnsweredIds(request, partType);

            List<Question> questions = new ArrayList<>();

            for (Question question : allQuestions) {
                if (!answeredIds.contains(
                        question.getQuestionId()
                )) {
                    questions.add(question);
                }
            }

            if (questions.isEmpty()) {
                request.removeAttribute("practiceFeedback");
                request.setAttribute(
                        "errorMessage",
                        "このPartの問題はすべて回答済みです。"
                                + "「練習をリセット」すると最初から解き直せます。"
                );
                request.getRequestDispatcher("/WEB-INF/practice.jsp")
                        .forward(request, response);
                return;
            }

            Question currentQuestion = questions.get(0);

            request.setAttribute("partType", partType);
            request.setAttribute("questionList", questions);
            request.setAttribute("currentQuestion", currentQuestion);

            request.setAttribute(
                    "standaloneQuestions",
                    currentQuestion.getGroupId() == null
                            ? List.of(currentQuestion)
                            : List.of()
            );

            if (partType == 5
                    || currentQuestion.getGroupId() == null) {
                request.setAttribute("passageGroups", List.of());
            } else {
                List<Question> currentGroupQuestions =
                        new ArrayList<>();

                for (Question question : questions) {
                    if (currentQuestion.getGroupId().equals(
                            question.getGroupId()
                    )) {
                        currentGroupQuestions.add(question);
                    }
                }

                PassageQuestionGroup group =
                        new PassageQuestionGroup(
                                new PassageGroupDao().getGroupById(
                                        currentQuestion.getGroupId()
                                ),
                                currentGroupQuestions
                        );

                request.setAttribute(
                        "passageGroups",
                        List.of(group)
                );
            }

            Object feedback =
                    request.getSession().getAttribute(
                            "practiceFeedback"
                    );
            if (feedback != null) {
                request.setAttribute("practiceFeedback", feedback);
            }

            request.getRequestDispatcher("/WEB-INF/practice.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "練習問題を取得できませんでした",
                    e
            );
        }
    }

    private Set<Long> getAnsweredIds(
            HttpServletRequest request,
            int partType
    ) {
        Object value = request.getSession().getAttribute(
                "practiceAnsweredIds_part_" + partType
        );

        if (value instanceof Set<?> set) {
            Set<Long> result = new HashSet<>();

            for (Object item : set) {
                if (item instanceof Long id) {
                    result.add(id);
                }
            }
            return result;
        }

        return new HashSet<>();
    }

    private void sessionFeedbackClean(
            HttpServletRequest request
    ) {
        request.getSession().removeAttribute("practiceFeedback");
    }
}