<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Practice Mode</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/lib/style.css">
</head>
<body>
<main class="practice-page">

    <c:if test="${not empty errorMessage}">
        <c:choose>
            <c:when test="${practiceCompleted}">
                <p class="completion-message">
                    <c:out value="${errorMessage}"/>
                </p>
            </c:when>
            <c:otherwise>
                <p class="error-message">
                    <c:out value="${errorMessage}"/>
                </p>
            </c:otherwise>
        </c:choose>
    </c:if>

    <c:choose>
        <c:when test="${empty partType}">
            <section class="practice-selection">
                <h1>Practice Mode</h1>
                <p>Select a part to practice.</p>

                <form action="${pageContext.request.contextPath}/PracticeServlet"
                      method="get"
                      class="part-form">
                    <label class="part-option">
                        <input type="radio"
                               name="partType"
                               value="5"
                               checked>
                        Part 5 - Incomplete Sentences
                    </label>

                    <label class="part-option">
                        <input type="radio"
                               name="partType"
                               value="6">
                        Part 6 - Text Completion
                    </label>

                    <label class="part-option">
                        <input type="radio"
                               name="partType"
                               value="7">
                        Part 7 - Reading Comprehension
                    </label>

                    <button class="start-button"
                            type="submit">
                        Start
                    </button>
                </form>
            </section>
        </c:when>

        <c:otherwise>
            <header class="page-header">
                <h1>Practice - Part <c:out value="${partType}"/></h1>

                <nav class="practice-actions"
                     aria-label="Practice actions">
                    <a href="${pageContext.request.contextPath}/PracticeResultsServlet">
                        View practice results
                    </a>

                    <form action="${pageContext.request.contextPath}/PracticeServlet"
                          method="get"
                          class="practice-reset-form">
                        <input type="hidden"
                               name="reset"
                               value="true">
                        <input type="hidden"
                               name="partType"
                               value="${partType}">
                        <button class="reset-button"
                                type="submit">
                            Reset answer
                        </button>
                    </form>
                </nav>
            </header>

            <c:if test="${not empty practiceFeedback}">
                <section class="answer-result
                        ${practiceFeedback.correct
                                ? 'result-correct'
                                : 'result-wrong'}"
                         aria-live="polite">

                    <h2>
                        <c:choose>
                            <c:when test="${practiceFeedback.correct}">
                                正解
                            </c:when>
                            <c:otherwise>
                                不正解
                            </c:otherwise>
                        </c:choose>
                    </h2>

                    <p>
                        あなたの回答：
                        <strong>
                            <c:out value="${practiceFeedback.answer}"/>
                        </strong>
                    </p>

                    <p>
                        正答：
                        <strong>
                            <c:out value="${practiceFeedback.correctAnswer}"/>
                        </strong>
                    </p>

                    <p class="explanation">
                        解説：
                        <c:out value="${practiceFeedback.explanation}"/>
                    </p>
                </section>
            </c:if>

            <c:choose>
                <c:when test="${practiceCompleted}">
                    <section class="completion-message">
                        <h2>このPartの問題はすべて回答済みです</h2>
                        <p>
                            練習結果は保存されています。
                            「練習をリセット」を選ぶと、
                            同じPartを最初から解き直せます。
                        </p>
                    </section>
                </c:when>

                <c:when test="${not empty standaloneQuestions}">
                    <c:forEach items="${standaloneQuestions}"
                               var="question">
                        <article class="question-card"
                                 id="question-${question.questionId}">
                            <div class="question-header">
                                <h2>Question</h2>

                                <span class="part-badge">
                                    Part
                                    <c:out value="${question.partType}"/>
                                </span>
                            </div>

                            <p class="question-text">
                                <c:out value="${question.questionText}"/>
                            </p>

                            <form action="${pageContext.request.contextPath}/PracticeResultRegistrationServlet"
                                  method="post"
                                  class="answer-form">

                                <input type="hidden"
                                       name="questionId"
                                       value="${question.questionId}">
                                <input type="hidden"
                                       name="partType"
                                       value="${question.partType}">

                                <label class="answer-option">
                                    <input type="radio"
                                           name="answer"
                                           value="A"
                                           required>
                                    <span>A.</span>
                                    <span>
                                        <c:out value="${question.optionA}"/>
                                    </span>
                                </label>

                                <label class="answer-option">
                                    <input type="radio"
                                           name="answer"
                                           value="B">
                                    <span>B.</span>
                                    <span>
                                        <c:out value="${question.optionB}"/>
                                    </span>
                                </label>

                                <label class="answer-option">
                                    <input type="radio"
                                           name="answer"
                                           value="C">
                                    <span>C.</span>
                                    <span>
                                        <c:out value="${question.optionC}"/>
                                    </span>
                                </label>

                                <label class="answer-option">
                                    <input type="radio"
                                           name="answer"
                                           value="D">
                                    <span>D.</span>
                                    <span>
                                        <c:out value="${question.optionD}"/>
                                    </span>
                                </label>

                                <button class="answer-button"
                                        type="submit">
                                    回答して次の問題へ
                                </button>
                            </form>
                        </article>
                    </c:forEach>
                </c:when>

                <c:when test="${not empty passageGroups}">
                    <c:forEach items="${passageGroups}"
                               var="group">

                        <section class="passage-card">
                            <span class="passage-type-badge">
                                <c:out value="${group.passage.groupType}"/>
                                PASSAGE
                                (
                                <c:out value="${group.passage.itemCount}"/>
                                documents
                                )
                            </span>

                            <h2>
                                <c:out value="${group.passage.title}"/>
                            </h2>

                            <c:forEach items="${group.passage.items}"
                                       var="document">
                                <article class="passage-item">
                                    <header class="document-header">
                                        <span class="document-number">
                                            Document
                                            <c:out value="${document.sequence_no}"/>
                                            of
                                            <c:out value="${group.passage.itemCount}"/>
                                        </span>

                                        <c:if test="${not empty document.speaker}">
                                            <h3>
                                                <c:out value="${document.speaker}"/>
                                            </h3>
                                        </c:if>
                                    </header>

                                    <pre class="passage-text"><c:out value="${document.textBody}"/></pre>
                                </article>
                            </c:forEach>
                        </section>

                        <c:forEach items="${group.questions}"
                                   var="question">
                            <c:if test="${question.questionId == currentQuestion.questionId}">
                                <article class="question-card"
                                         id="question-${question.questionId}">
                                    <div class="question-header">
                                        <h2>
                                            Question
                                            <c:out value="${question.sequenceNo}"/>
                                        </h2>

                                        <span class="part-badge">
                                            Part
                                            <c:out value="${question.partType}"/>
                                        </span>
                                    </div>

                                    <p class="question-text">
                                        <c:out value="${question.questionText}"/>
                                    </p>

                                    <form action="${pageContext.request.contextPath}/PracticeResultRegistrationServlet"
                                          method="post"
                                          class="answer-form">

                                        <input type="hidden"
                                               name="questionId"
                                               value="${question.questionId}">
                                        <input type="hidden"
                                               name="partType"
                                               value="${question.partType}">

                                        <label class="answer-option">
                                            <input type="radio"
                                                   name="answer"
                                                   value="A"
                                                   required>
                                            <span>A.</span>
                                            <span>
                                                <c:out value="${question.optionA}"/>
                                            </span>
                                        </label>

                                        <label class="answer-option">
                                            <input type="radio"
                                                   name="answer"
                                                   value="B">
                                            <span>B.</span>
                                            <span>
                                                <c:out value="${question.optionB}"/>
                                            </span>
                                        </label>

                                        <label class="answer-option">
                                            <input type="radio"
                                                   name="answer"
                                                   value="C">
                                            <span>C.</span>
                                            <span>
                                                <c:out value="${question.optionC}"/>
                                            </span>
                                        </label>

                                        <label class="answer-option">
                                            <input type="radio"
                                                   name="answer"
                                                   value="D">
                                            <span>D.</span>
                                            <span>
                                                <c:out value="${question.optionD}"/>
                                            </span>
                                        </label>

                                        <button class="answer-button"
                                                type="submit">
                                            回答して次の問題へ
                                        </button>
                                    </form>
                                </article>
                            </c:if>
                        </c:forEach>
                    </c:forEach>
                </c:when>

                <c:otherwise>
                    <section class="completion-message">
                        <h2>表示できる問題がありません</h2>
                        <p>Part選択からやり直してください。</p>

                        <a href="${pageContext.request.contextPath}/PracticeServlet">
                            Part選択へ戻る
                        </a>
                    </section>
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>

    <a class="back-link"
       href="${pageContext.request.contextPath}/index.jsp">
        Back to mode selection
    </a>
</main>
</body>
</html>