<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Practice Results</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/lib/style.css">
</head>
<body>
<main class="results-page">
    <header class="page-header">
        <h1>Practice Results</h1>
        <nav class="result-links">
            <a href="${pageContext.request.contextPath}/ResultsCsvServlet?type=practice">
                Download CSV
            </a>
            <a href="${pageContext.request.contextPath}/PracticeServlet">
                Continue practicing
            </a>
        </nav>
    </header>

    <c:if test="${not empty selectedRecord}">
        <section class="exam-result-card">
            <p class="result-meta">
                Part <c:out value="${selectedRecord.partType}"/>
                - Question <c:out value="${selectedRecord.questionId}"/>
                - <c:out value="${selectedRecord.answeredAt}"/>
            </p>
            <h2><c:out value="${selectedRecord.questionText}"/></h2>
            <p>
                Your answer:
                <strong><c:out value="${selectedRecord.userAnswer}"/></strong>
                <span aria-hidden="true">|</span>
                Correct answer:
                <strong><c:out value="${selectedRecord.correctAnswer}"/></strong>
            </p>
            <c:choose>
                <c:when test="${selectedRecord.correct}">
                    <p class="result-correct answer-result">Correct</p>
                </c:when>
                <c:otherwise>
                    <p class="result-wrong answer-result">Incorrect</p>
                </c:otherwise>
            </c:choose>
            <c:if test="${not empty selectedRecord.explanation}">
                <p class="explanation">
                    Explanation:
                    <c:out value="${selectedRecord.explanation}"/>
                </p>
            </c:if>
        </section>
    </c:if>

    <section class="results-list">
        <h2>Recent attempts</h2>
        <c:choose>
            <c:when test="${empty historyRecords}">
                <p>No practice attempts have been recorded.</p>
            </c:when>
            <c:otherwise>
                <div class="table-scroll">
                    <table>
                        <thead>
                            <tr>
                                <th>Answered at</th>
                                <th>Part</th>
                                <th>Question</th>
                                <th>Your answer</th>
                                <th>Correct answer</th>
                                <th>Result</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${historyRecords}" var="record">
                                <tr>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/PracticeResultsServlet?historyId=${record.historyId}">
                                            <c:out value="${record.answeredAt}"/>
                                        </a>
                                    </td>
                                    <td><c:out value="${record.partType}"/></td>
                                    <td><c:out value="${record.questionText}"/></td>
                                    <td><c:out value="${record.userAnswer}"/></td>
                                    <td><c:out value="${record.correctAnswer}"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${record.correct}">Correct</c:when>
                                            <c:otherwise>Incorrect</c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</main>
</body>
</html>
