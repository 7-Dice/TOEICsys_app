package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import beans.PassageGroup;
import beans.Question;
import dao.PassageGroupDao;
import dao.QuestionDao;

@WebServlet("/PracticeControllerServlet")
public class PracticeControllerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String mode = request.getParameter("mode");
        String action = request.getParameter("action");

        if ("reset".equals(action)) {
            request.getSession().removeAttribute("practiceResultMap");
            request.getSession().removeAttribute("practiceSelectedAnswerMap");

            response.sendRedirect(
                    request.getContextPath() + "/index.jsp");
            return;
        }

        if (!"practice".equals(mode)) {
            if ("exam".equals(mode)) {
                response.sendRedirect(
                        request.getContextPath() + "/exam.jsp");
                return;
            }

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "不明なモードです");
            return;
        }

        String partTypeParam = request.getParameter("partType");

        if (partTypeParam == null || partTypeParam.isBlank()) {
            request.getRequestDispatcher("/WEB-INF/practice.jsp")
                    .forward(request, response);
            return;
        }

        int partType;

        try {
            partType = Integer.parseInt(partTypeParam);
        } catch (NumberFormatException e) {
            request.setAttribute(
                    "errMsg",
                    "パート番号が正しくありません");
            request.getRequestDispatcher("/WEB-INF/practice.jsp")
                    .forward(request, response);
            return;
        }

        if (partType < 5 || partType > 7) {
            request.setAttribute(
                    "errMsg",
                    "Part 5～7を選択してください");
            request.getRequestDispatcher("/WEB-INF/practice.jsp")
                    .forward(request, response);
            return;
        }

        try {
            QuestionDao questionDao = new QuestionDao();

            List<Question> questionList =
                    questionDao.getQuestionsForPractice(partType, false);

            if (questionList.isEmpty()) {
                request.setAttribute(
                        "errMsg",
                        "問題データが存在しません");
                request.getRequestDispatcher("/WEB-INF/practice.jsp")
                        .forward(request, response);
                return;
            }

            PassageGroupDao passageGroupDao = new PassageGroupDao();
            Map<Long, PassageGroup> passageMap = new HashMap<>();

            for (Question question : questionList) {
                Long groupId = question.getGroupId();

                if (groupId != null
                        && groupId > 0
                        && !passageMap.containsKey(groupId)) {

                    PassageGroup group =
                            passageGroupDao.getGroupById(groupId);

                    if (group != null) {
                        passageMap.put(groupId, group);
                    }
                }
            }

            if ("answer".equals(action)) {
                String questionIdParam =
                        request.getParameter("questionId");
                String selectedAnswer =
                        request.getParameter("answer");

                if (questionIdParam == null
                        || questionIdParam.isBlank()
                        || selectedAnswer == null
                        || selectedAnswer.isBlank()) {

                    request.setAttribute(
                            "errMsg",
                            "問題IDまたは回答を指定してください");
                } else {
                    try {
                        long questionId =
                                Long.parseLong(questionIdParam);

                        Question answeredQuestion =
                                questionDao.getQuestionById(questionId);

                        if (answeredQuestion == null) {
                            request.setAttribute(
                                    "errMsg",
                                    "問題データなし");
                        } else {
                            boolean correct =
                                    selectedAnswer.equalsIgnoreCase(
                                            answeredQuestion.getCorrectAnswer());

                            @SuppressWarnings("unchecked")
                            Map<Long, Boolean> resultMap =
                                    (Map<Long, Boolean>) request
                                            .getSession()
                                            .getAttribute("practiceResultMap");

                            if (resultMap == null) {
                                resultMap = new HashMap<>();
                            }

                            resultMap.put(questionId, correct);
                            request.getSession().setAttribute(
                                    "practiceResultMap",
                                    resultMap);

                            @SuppressWarnings("unchecked")
                            Map<Long, String> selectedAnswerMap =
                                    (Map<Long, String>) request
                                            .getSession()
                                            .getAttribute("practiceSelectedAnswerMap");

                            if (selectedAnswerMap == null) {
                                selectedAnswerMap = new HashMap<>();
                            }

                            selectedAnswerMap.put(
                                    questionId,
                                    selectedAnswer);
                            request.getSession().setAttribute(
                                    "practiceSelectedAnswerMap",
                                    selectedAnswerMap);

                            response.sendRedirect(
                                    request.getContextPath()
                                            + "/PracticeControllerServlet"
                                            + "?mode=practice"
                                            + "&partType=" + partTypeParam
                                            + "#question-" + questionId);
                            return;
                        }
                    } catch (NumberFormatException e) {
                        request.setAttribute(
                                "errMsg",
                                "問題ID不正");
                    }
                }
            }

            request.setAttribute("questionList", questionList);
            request.setAttribute("passageMap", passageMap);
            request.setAttribute(
                    "resultMap",
                    request.getSession().getAttribute("practiceResultMap"));
            request.setAttribute(
                    "selectedAnswerMap",
                    request.getSession().getAttribute("practiceSelectedAnswerMap"));

            request.getRequestDispatcher("/WEB-INF/practice.jsp")
                    .forward(request, response);
            return;

        } catch (SQLException e) {
            throw new ServletException(
                    "DB処理に失敗しました",
                    e);
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        doGet(request, response);
    }
}