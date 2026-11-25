package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import beans.MockExamResult;
import dao.MockExamDao;

@WebServlet("/MockExamResultsServlet")
public class MockExamResultsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        String mockIdParameter = request.getParameter("mockId");
        try {
            MockExamDao resultDao = new MockExamDao();
            MockExamResult selectedResult = null;
            if (mockIdParameter != null && !mockIdParameter.isBlank()) {
                long mockId = Long.parseLong(mockIdParameter);
                selectedResult = resultDao.findById(mockId);
                if (selectedResult == null) {
                    response.sendError(
                            HttpServletResponse.SC_NOT_FOUND,
                            "模試結果が見つかりません"
                    );
                    return;
                }
            }

            List<MockExamResult> results = resultDao.findRecent(100);
            request.setAttribute("selectedResult", selectedResult);
            request.setAttribute("results", results);
            request.getRequestDispatcher(
                    "/WEB-INF/mock-exam-results.jsp"
            ).forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "模試結果IDが正しくありません"
            );
        } catch (SQLException e) {
            throw new ServletException("模試結果を取得できませんでした", e);
        }
    }
}
