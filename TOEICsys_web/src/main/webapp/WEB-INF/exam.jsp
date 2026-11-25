<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Reading Mock Exam</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/lib/style.css">
</head>
<body>
<main class="exam-page">
    <header class="exam-header">
        <div>
            <h1>Reading Mock Exam</h1>
            <p>100 questions - Parts 5, 6, and 7 - 75 minutes</p>
            <p class="exam-note">
                Listening is excluded. Unanswered questions are allowed.
            </p>
        </div>
        <div class="exam-timer" aria-live="polite">
            Time remaining
            <strong id="exam-timer"
                    data-deadline="${deadlineMillis}">75:00</strong>
        </div>
    </header>

    <form id="exam-form"
          action="${pageContext.request.contextPath}/MockExamResultRegistrationServlet"
          method="post">
        <h2 class="exam-part-heading">
            Part 5 <span>Incomplete Sentences</span>
        </h2>
        <c:forEach items="${part5Questions}" var="question">
            <article class="question-card exam-question-card"
                     id="question-${question.questionId}">
                <header class="question-header">
                    <h3>Question <c:out value="${questionNumbers[question.questionId]}"/></h3>
                </header>
                <p class="question-text">
                    <c:out value="${question.questionText}"/>
                </p>
                <label class="answer-option">
                    <input type="radio" name="answer_${question.questionId}" value="A">
                    <span>A.</span><span><c:out value="${question.optionA}"/></span>
                </label>
                <label class="answer-option">
                    <input type="radio" name="answer_${question.questionId}" value="B">
                    <span>B.</span><span><c:out value="${question.optionB}"/></span>
                </label>
                <label class="answer-option">
                    <input type="radio" name="answer_${question.questionId}" value="C">
                    <span>C.</span><span><c:out value="${question.optionC}"/></span>
                </label>
                <label class="answer-option">
                    <input type="radio" name="answer_${question.questionId}" value="D">
                    <span>D.</span><span><c:out value="${question.optionD}"/></span>
                </label>
            </article>
        </c:forEach>

        <h2 class="exam-part-heading">
            Part 6 <span>Text Completion</span>
        </h2>
        <c:forEach items="${part6Groups}" var="group">
            <section class="passage-card exam-passage-card">
                <span class="passage-type-badge">
                    <c:out value="${group.passage.groupType}"/> PASSAGE
                    (<c:out value="${group.passage.itemCount}"/> documents)
                </span>
                <h3><c:out value="${group.passage.title}"/></h3>
                <c:forEach items="${group.passage.items}" var="document">
                    <article class="passage-item">
                        <header class="document-header">
                            <span class="document-number">
                                Document <c:out value="${document.sequence_no}"/>
                                of <c:out value="${group.passage.itemCount}"/>
                            </span>
                            <h3><c:out value="${document.speaker}"/></h3>
                        </header>
                        <pre class="passage-text"><c:out value="${document.textBody}"/></pre>
                    </article>
                </c:forEach>
            </section>
            <c:forEach items="${group.questions}" var="question">
                <article class="question-card exam-question-card"
                         id="question-${question.questionId}">
                    <header class="question-header">
                        <h3>Question <c:out value="${questionNumbers[question.questionId]}"/></h3>
                    </header>
                    <p class="question-text">
                        <c:out value="${question.questionText}"/>
                    </p>
                    <label class="answer-option">
                        <input type="radio" name="answer_${question.questionId}" value="A">
                        <span>A.</span><span><c:out value="${question.optionA}"/></span>
                    </label>
                    <label class="answer-option">
                        <input type="radio" name="answer_${question.questionId}" value="B">
                        <span>B.</span><span><c:out value="${question.optionB}"/></span>
                    </label>
                    <label class="answer-option">
                        <input type="radio" name="answer_${question.questionId}" value="C">
                        <span>C.</span><span><c:out value="${question.optionC}"/></span>
                    </label>
                    <label class="answer-option">
                        <input type="radio" name="answer_${question.questionId}" value="D">
                        <span>D.</span><span><c:out value="${question.optionD}"/></span>
                    </label>
                </article>
            </c:forEach>
        </c:forEach>

        <h2 class="exam-part-heading">
            Part 7 <span>Reading Comprehension</span>
        </h2>
        <c:forEach items="${part7Groups}" var="group">
            <section class="passage-card exam-passage-card">
                <span class="passage-type-badge">
                    <c:out value="${group.passage.groupType}"/> PASSAGE
                    (<c:out value="${group.passage.itemCount}"/> documents)
                </span>
                <h3><c:out value="${group.passage.title}"/></h3>
                <c:forEach items="${group.passage.items}" var="document">
                    <article class="passage-item">
                        <header class="document-header">
                            <span class="document-number">
                                Document <c:out value="${document.sequence_no}"/>
                                of <c:out value="${group.passage.itemCount}"/>
                            </span>
                            <h3><c:out value="${document.speaker}"/></h3>
                        </header>
                        <pre class="passage-text"><c:out value="${document.textBody}"/></pre>
                    </article>
                </c:forEach>
            </section>
            <c:forEach items="${group.questions}" var="question">
                <article class="question-card exam-question-card"
                         id="question-${question.questionId}">
                    <header class="question-header">
                        <h3>Question <c:out value="${questionNumbers[question.questionId]}"/></h3>
                    </header>
                    <p class="question-text">
                        <c:out value="${question.questionText}"/>
                    </p>
                    <label class="answer-option">
                        <input type="radio" name="answer_${question.questionId}" value="A">
                        <span>A.</span><span><c:out value="${question.optionA}"/></span>
                    </label>
                    <label class="answer-option">
                        <input type="radio" name="answer_${question.questionId}" value="B">
                        <span>B.</span><span><c:out value="${question.optionB}"/></span>
                    </label>
                    <label class="answer-option">
                        <input type="radio" name="answer_${question.questionId}" value="C">
                        <span>C.</span><span><c:out value="${question.optionC}"/></span>
                    </label>
                    <label class="answer-option">
                        <input type="radio" name="answer_${question.questionId}" value="D">
                        <span>D.</span><span><c:out value="${question.optionD}"/></span>
                    </label>
                </article>
            </c:forEach>
        </c:forEach>

        <button id="submit-exam" class="entry-button" type="submit">
            Submit exam
        </button>
    </form>

    <script>
        (function () {
            var timer = document.getElementById("exam-timer");
            var deadline = Number(timer.dataset.deadline);
            var form = document.getElementById("exam-form");
            var submitButton = document.getElementById("submit-exam");
            var submitted = false;

            function updateTimer() {
                var seconds = Math.max(
                    0,
                    Math.ceil((deadline - Date.now()) / 1000)
                );
                timer.textContent =
                    String(Math.floor(seconds / 60)).padStart(2, "0")
                    + ":"
                    + String(seconds % 60).padStart(2, "0");

                if (seconds === 0 && !submitted) {
                    submitted = true;
                    form.submit();
                }
            }

            form.addEventListener("submit", function () {
                submitted = true;
                submitButton.disabled = true;
            });
            updateTimer();
            window.setInterval(updateTimer, 1000);
        }());
    </script>
</main>
</body>
</html>
