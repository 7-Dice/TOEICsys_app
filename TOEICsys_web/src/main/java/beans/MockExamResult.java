package beans;

import java.sql.Timestamp;
import java.util.Locale;

public class MockExamResult {
    private final long mockId;
    private final Timestamp startTime;
    private final Timestamp endTime;
    private final int totalQuestions;
    private final int answeredCount;
    private final int correctCount;
    private final int incorrectCount;
    private final int unansweredCount;

    public MockExamResult(
            long mockId,
            Timestamp startTime,
            Timestamp endTime,
            int totalQuestions,
            int answeredCount,
            int correctCount,
            int incorrectCount,
            int unansweredCount
    ) {
        this.mockId = mockId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalQuestions = totalQuestions;
        this.answeredCount = answeredCount;
        this.correctCount = correctCount;
        this.incorrectCount = incorrectCount;
        this.unansweredCount = unansweredCount;
    }

    public long getMockId() {
        return mockId;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getAnsweredCount() {
        return answeredCount;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public int getIncorrectCount() {
        return incorrectCount;
    }

    public int getUnansweredCount() {
        return unansweredCount;
    }

    public long getElapsedSeconds() {
        return Math.max(
                0,
                (endTime.getTime() - startTime.getTime()) / 1000
        );
    }

    public int getScorePercent() {
        return totalQuestions == 0
                ? 0
                : Math.round(correctCount * 100.0f / totalQuestions);
    }

    public String getElapsedDisplay() {
        long seconds = getElapsedSeconds();
        return String.format(
                Locale.ROOT,
                "%02d:%02d",
                seconds / 60,
                seconds % 60
        );
    }
}
