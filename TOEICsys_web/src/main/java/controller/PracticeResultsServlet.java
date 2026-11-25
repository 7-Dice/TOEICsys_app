package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import beans.LearningHistoryRecord;
import dao.LearningHistoryDao;

@WebServlet("/PracticeResultsServlet")
public class PracticeResultsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        String historyIdParameter = request.getParameter("historyId");
        try {
            LearningHistoryDao historyDao = new LearningHistoryDao();
            LearningHistoryRecord selectedRecord = null;
            if (historyIdParameter != null
                    && !historyIdParameter.isBlank()) {
                long historyId = Long.parseLong(historyIdParameter);
                selectedRecord = historyDao.findById(historyId);
                if (selectedRecord == null) {
                    response.sendError(
                            HttpServletResponse.SC_NOT_FOUND,
                            "練習結果が見つかりません"
                    );
                    return;
                }
            }

            List<LearningHistoryRecord> historyRecords =
                    historyDao.findRecent(100);
            request.setAttribute("selectedRecord", selectedRecord);
            request.setAttribute("historyRecords", historyRecords);
            request.getRequestDispatcher("/WEB-INF/result.jsp")
                    .forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "結果IDが正しくありません"
            );
        } catch (SQLException e) {
            throw new ServletException("練習結果を取得できませんでした", e);
        }
    }
}
