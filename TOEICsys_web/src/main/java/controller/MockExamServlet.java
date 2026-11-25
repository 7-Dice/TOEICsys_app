package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import beans.PassageQuestionGroup;
import beans.Question;
import dao.PassageGroupDao;
import dao.QuestionDao;

@WebServlet("/MockExamServlet")
public class MockExamServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int TIME_LIMIT_SECONDS = 75 * 60;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        HttpSession session = request.getSession();
        List<Question> questions = getActiveQuestions(session);
        Timestamp startTime = (Timestamp) session.getAttribute(
                "mockExamStartTime"
        );

        try {
            if (questions == null || startTime == null) {
                questions =
                        new QuestionDao().getRandomMockExamQuestions();
                startTime = new Timestamp(System.currentTimeMillis());
                session.setAttribute("mockExamQuestions", questions);
                session.setAttribute("mockExamStartTime", startTime);
            }

            List<Question> part5Questions = new ArrayList<>();
            Map<Long, Integer> questionNumbers = new HashMap<>();
            for (int i = 0; i < questions.size(); i++) {
                Question question = questions.get(i);
                questionNumbers.put(question.getQuestionId(), i + 1);
                if (question.getPartType() == 5) {
                    part5Questions.add(question);
                }
            }

            PassageGroupDao passageGroupDao = new PassageGroupDao();
            List<PassageQuestionGroup> part6Groups =
                    PassageQuestionGrouper.group(
                            questions,
                            6,
                            passageGroupDao
                    );
            List<PassageQuestionGroup> part7Groups =
                    PassageQuestionGrouper.group(
                            questions,
                            7,
                            passageGroupDao
                    );

            request.setAttribute("part5Questions", part5Questions);
            request.setAttribute("part6Groups", part6Groups);
            request.setAttribute("part7Groups", part7Groups);
            request.setAttribute("questionNumbers", questionNumbers);
            request.setAttribute(
                    "deadlineMillis",
                    startTime.getTime() + TIME_LIMIT_SECONDS * 1000L
            );
            request.getRequestDispatcher("/WEB-INF/exam.jsp")
                    .forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("模試問題を取得できませんでした", e);
        }
    }

    private List<Question> getActiveQuestions(HttpSession session) {
        Object value = session.getAttribute("mockExamQuestions");
        if (!(value instanceof List<?> list)) {
            return null;
        }

        List<Question> questions = new ArrayList<>();
        for (Object item : list) {
            if (!(item instanceof Question question)) {
                return null;
            }
            questions.add(question);
        }
        return questions;
    }
}
