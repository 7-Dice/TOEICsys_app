package beans;

import java.sql.Timestamp;

public class LearningHistoryRecord {
    private final long historyId;
    private final long questionId;
    private final int partType;
    private final String questionText;
    private final String userAnswer;
    private final String correctAnswer;
    private final Boolean correct;
    private final Timestamp answeredAt;
    private final String explanation;

    public LearningHistoryRecord(
            long historyId,
            long questionId,
            int partType,
            String questionText,
            String userAnswer,
            String correctAnswer,
            Boolean correct,
            Timestamp answeredAt,
            String explanation
    ) {
        this.historyId = historyId;
        this.questionId = questionId;
        this.partType = partType;
        this.questionText = questionText;
        this.userAnswer = userAnswer;
        this.correctAnswer = correctAnswer;
        this.correct = correct;
        this.answeredAt = answeredAt;
        this.explanation = explanation;
    }

    public long getHistoryId() {
        return historyId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public int getPartType() {
        return partType;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public Timestamp getAnsweredAt() {
        return answeredAt;
    }

    public String getExplanation() {
        return explanation;
    }
}
