package beans;

import java.util.List;

public class PassageQuestionGroup {
    private final PassageGroup passage;
    private final List<Question> questions;

    public PassageQuestionGroup(
            PassageGroup passage,
            List<Question> questions
    ) {
        this.passage = passage;
        this.questions = questions;
    }

    public PassageGroup getPassage() {
        return passage;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}