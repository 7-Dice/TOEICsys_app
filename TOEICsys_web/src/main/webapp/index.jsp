<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>TOEIC Learning System</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/lib/style.css">
</head>
<body>
    <main class="entry-page">
        <section class="entry-form">
            <h1>TOEIC Learning System</h1>
            <p class="entry-description">
                Choose a learning mode or review your previous results.
            </p>

            <div class="mode-options">
                <section class="mode-option">
                    <span class="mode-name">Practice Mode</span>
                    <span class="mode-description">
                        Practice by part. Previously incorrect questions are prioritized.
                    </span>
                    <a class="entry-button"
                       href="${pageContext.request.contextPath}/PracticeServlet">
                        Start practice
                    </a>
                </section>

                <section class="mode-option">
                    <span class="mode-name">Reading Mock Exam</span>
                    <span class="mode-description">
                        Parts 5-7, 100 questions, 75 minutes. Listening is excluded.
                    </span>
                    <a class="entry-button"
                       href="${pageContext.request.contextPath}/MockExamServlet">
                        Start mock exam
                    </a>
                </section>
            </div>

            <nav class="result-links" aria-label="Result pages">
                <a href="${pageContext.request.contextPath}/PracticeResultsServlet">
                    Practice results
                </a>
                <a href="${pageContext.request.contextPath}/MockExamResultsServlet">
                    Mock exam results
                </a>
            </nav>
        </section>
    </main>
</body>
</html>
