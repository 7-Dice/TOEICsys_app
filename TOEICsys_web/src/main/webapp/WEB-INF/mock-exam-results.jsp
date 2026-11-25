<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mock Exam Results</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/lib/style.css">
</head>
<body>
<main class="results-page">
    <header class="page-header">
        <h1>Mock Exam Results</h1>
        <nav class="result-links">
            <a href="${pageContext.request.contextPath}/ResultsCsvServlet?type=mock">
                Download CSV
            </a>
            <a href="${pageContext.request.contextPath}/MockExamServlet">
                Start another exam
            </a>
        </nav>
    </header>

    <c:if test="${not empty selectedResult}">
        <section class="exam-result-card">
            <p class="result-meta">
                Exam #<c:out value="${selectedResult.mockId}"/>
                - <c:out value="${selectedResult.startTime}"/>
            </p>
            <p class="exam-score">
                <c:out value="${selectedResult.correctCount}"/> /
                <c:out value="${selectedResult.totalQuestions}"/>
                (<c:out value="${selectedResult.scorePercent}"/>%)
            </p>
            <dl class="exam-result-list">
                <dt>Answered</dt>
                <dd><c:out value="${selectedResult.answeredCount}"/></dd>
                <dt>Correct</dt>
                <dd><c:out value="${selectedResult.correctCount}"/></dd>
                <dt>Incorrect</dt>
                <dd><c:out value="${selectedResult.incorrectCount}"/></dd>
                <dt>Unanswered</dt>
                <dd><c:out value="${selectedResult.unansweredCount}"/></dd>
                <dt>Time used</dt>
                <dd><c:out value="${selectedResult.elapsedDisplay}"/></dd>
            </dl>
            <p class="exam-note">
                Listening is excluded. This percentage is not an official TOEIC scaled score.
            </p>
        </section>
    </c:if>

    <section class="results-list">
        <h2>Recent exams</h2>
        <c:choose>
            <c:when test="${empty results}">
                <p>No mock exam results have been recorded.</p>
            </c:when>
            <c:otherwise>
                <div class="table-scroll">
                    <table>
                        <thead>
                            <tr>
                                <th>Started</th>
                                <th>Answered</th>
                                <th>Correct</th>
                                <th>Incorrect</th>
                                <th>Unanswered</th>
                                <th>Result</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${results}" var="result">
                                <tr>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/MockExamResultsServlet?mockId=${result.mockId}">
                                            <c:out value="${result.startTime}"/>
                                        </a>
                                    </td>
                                    <td><c:out value="${result.answeredCount}"/></td>
                                    <td><c:out value="${result.correctCount}"/></td>
                                    <td><c:out value="${result.incorrectCount}"/></td>
                                    <td><c:out value="${result.unansweredCount}"/></td>
                                    <td><c:out value="${result.scorePercent}"/>%</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </section>

    <a class="back-link" href="${pageContext.request.contextPath}/index.jsp">
        Back to mode selection
    </a>
</main>
</body>
</html>
