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
import jakarta.servlet.http.HttpSession;

import beans.PassageGroup;
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

        HttpSession session = request.getSession();

        String partParameter =
                request.getParameter("partType");

        // Part未指定の場合は選択画面
        if (partParameter == null || partParameter.isBlank()) {
            session.removeAttribute("practiceFeedback");
            request.getRequestDispatcher("/WEB-INF/practice.jsp")
                    .forward(request, response);
            return;
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

        // 練習リセット
        if ("true".equals(request.getParameter("reset"))) {
            session.removeAttribute(
                    "practiceAnsweredIds_part_" + partType
            );
            session.removeAttribute("practiceFeedback");
        }

        try {
            List<Question> allQuestions =
                    new QuestionDao()
                            .getQuestionsForPractice(partType, true);

            if (allQuestions.isEmpty()) {
                request.setAttribute("partType", partType);
                request.setAttribute(
                        "errorMessage",
                        "問題データがありません"
                );
                request.getRequestDispatcher("/WEB-INF/practice.jsp")
                        .forward(request, response);
                return;
            }

            Set<Long> answeredIds =
                    getAnsweredIds(session, partType);

            List<Question> unansweredQuestions =
                    new ArrayList<>();

            for (Question question : allQuestions) {
                if (!answeredIds.contains(
                        question.getQuestionId()
                )) {
                    unansweredQuestions.add(question);
                }
            }

            if (unansweredQuestions.isEmpty()) {
                request.setAttribute("partType", partType);
                request.setAttribute("practiceCompleted", true);

                request.setAttribute(
                        "errorMessage",
                        "このPartの問題はすべて回答済みです。"
                                + "「練習をリセット」すると最初から解き直せます。"
                );

                request.getRequestDispatcher("/WEB-INF/practice.jsp")
                        .forward(request, response);
                return;
            }

            // 次に表示する問題を1問だけ選ぶ
            Question currentQuestion =
                    unansweredQuestions.get(0);

            request.setAttribute("partType", partType);
            request.setAttribute(
                    "questionList",
                    unansweredQuestions
            );
            request.setAttribute(
                    "currentQuestion",
                    currentQuestion
            );

            // Part 5または本文グループなしの問題
            if (partType == 5
                    || currentQuestion.getGroupId() == null) {
                request.setAttribute(
                        "standaloneQuestions",
                        List.of(currentQuestion)
                );
                request.setAttribute(
                        "passageGroups",
                        List.of()
                );
            } else {
                // 現在の本文グループだけを取得
                request.setAttribute(
                        "standaloneQuestions",
                        List.of()
                );

                PassageGroupDao passageGroupDao =
                        new PassageGroupDao();

                PassageGroup passage =
                        passageGroupDao.getGroupById(
                                currentQuestion.getGroupId()
                        );

                if (passage == null) {
                    throw new SQLException(
                            "本文グループが見つかりません: "
                                    + currentQuestion.getGroupId()
                    );
                }

                List<Question> groupQuestions =
                        new ArrayList<>();

                for (Question question : unansweredQuestions) {
                    Long groupId = question.getGroupId();

                    if (groupId != null
                            && groupId.equals(
                                    currentQuestion.getGroupId()
                            )) {
                        groupQuestions.add(question);
                    }
                }

                PassageQuestionGroup passageQuestionGroup =
                        new PassageQuestionGroup(
                                passage,
                                groupQuestions
                        );

                request.setAttribute(
                        "passageGroups",
                        List.of(passageQuestionGroup)
                );
            }

            // 回答直後のフィードバックをリクエストへ渡す
            Object feedback =
                    session.getAttribute("practiceFeedback");

            if (feedback != null) {
                request.setAttribute(
                        "practiceFeedback",
                        feedback
                );

                // 一度表示したらセッションから削除
                session.removeAttribute("practiceFeedback");
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
            HttpSession session,
            int partType
    ) {
        Object value =
                session.getAttribute(
                        "practiceAnsweredIds_part_" + partType
                );

        if (value instanceof Set<?> set) {
            Set<Long> answeredIds = new HashSet<>();

            for (Object item : set) {
                if (item instanceof Long id) {
                    answeredIds.add(id);
                }
            }

            return answeredIds;
        }

        return new HashSet<>();
    }
}