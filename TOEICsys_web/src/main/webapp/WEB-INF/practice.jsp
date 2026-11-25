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
        <p class="error-message"><c:out value="${errorMessage}"/></p>
    </c:if>

    <c:choose>
        <c:when test="${empty questionList}">
            <h1>Practice Mode</h1>
            <p>Select a part to practice.</p>
            <form action="${pageContext.request.contextPath}/PracticeServlet"
                  method="get"
                  class="part-form">
                <label class="part-option">
                    <input type="radio" name="partType" value="5" checked>
                    Part 5 - Incomplete Sentences
                </label>
                <label class="part-option">
                    <input type="radio" name="partType" value="6">
                    Part 6 - Text Completion
                </label>
                <label class="part-option">
                    <input type="radio" name="partType" value="7">
                    Part 7 - Reading Comprehension
                </label>
                <button class="start-button" type="submit">Start</button>
            </form>
        </c:when>

        <c:otherwise>
            <header class="page-header">
                <h1>Practice - Part <c:out value="${partType}"/></h1>
                <a href="${pageContext.request.contextPath}/PracticeResultsServlet">
                    View practice results
                </a>
            </header>

            <c:forEach items="${standaloneQuestions}" var="question"
                       varStatus="questionStatus">
                <article class="question-card"
                         id="question-${question.questionId}">
                    <h2>Question ${questionStatus.count}</h2>
                    <p class="question-text">
                        <c:out value="${question.questionText}"/>
                    </p>
                    <form action="${pageContext.request.contextPath}/PracticeResultRegistrationServlet"
                          method="post"
                          class="answer-form">
                        <input type="hidden" name="questionId"
                               value="${question.questionId}">
                        <input type="hidden" name="partType"
                               value="${question.partType}">
                        <label class="answer-option">
                            <input type="radio" name="answer" value="A" required>
                            <span>A.</span>
                            <span><c:out value="${question.optionA}"/></span>
                        </label>
                        <label class="answer-option">
                            <input type="radio" name="answer" value="B">
                            <span>B.</span>
                            <span><c:out value="${question.optionB}"/></span>
                        </label>
                        <label class="answer-option">
                            <input type="radio" name="answer" value="C">
                            <span>C.</span>
                            <span><c:out value="${question.optionC}"/></span>
                        </label>
                        <label class="answer-option">
                            <input type="radio" name="answer" value="D">
                            <span>D.</span>
                            <span><c:out value="${question.optionD}"/></span>
                        </label>
                        <button class="answer-button" type="submit">
                            Submit answer
                        </button>
                    </form>
                </article>
            </c:forEach>

            <c:forEach items="${passageGroups}" var="group">
                <section class="passage-card">
                    <span class="passage-type-badge">
                        <c:out value="${group.passage.groupType}"/> PASSAGE
                        (<c:out value="${group.passage.itemCount}"/> documents)
                    </span>
                    <h2><c:out value="${group.passage.title}"/></h2>

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
                    <article class="question-card"
                             id="question-${question.questionId}">
                        <h2>Question <c:out value="${question.sequenceNo}"/></h2>
                        <p class="question-text">
                            <c:out value="${question.questionText}"/>
                        </p>
                        <form action="${pageContext.request.contextPath}/PracticeResultRegistrationServlet"
                              method="post"
                              class="answer-form">
                            <input type="hidden" name="questionId"
                                   value="${question.questionId}">
                            <input type="hidden" name="partType"
                                   value="${question.partType}">
                            <label class="answer-option">
                                <input type="radio" name="answer" value="A" required>
                                <span>A.</span>
                                <span><c:out value="${question.optionA}"/></span>
                            </label>
                            <label class="answer-option">
                                <input type="radio" name="answer" value="B">
                                <span>B.</span>
                                <span><c:out value="${question.optionB}"/></span>
                            </label>
                            <label class="answer-option">
                                <input type="radio" name="answer" value="C">
                                <span>C.</span>
                                <span><c:out value="${question.optionC}"/></span>
                            </label>
                            <label class="answer-option">
                                <input type="radio" name="answer" value="D">
                                <span>D.</span>
                                <span><c:out value="${question.optionD}"/></span>
                            </label>
                            <button class="answer-button" type="submit">
                                Submit answer
                            </button>
                        </form>
                    </article>
                </c:forEach>
            </c:forEach>
        </c:otherwise>
    </c:choose>

    <a class="back-link" href="${pageContext.request.contextPath}/index.jsp">
        Back to mode selection
    </a>
</main>
</body>
</html>
