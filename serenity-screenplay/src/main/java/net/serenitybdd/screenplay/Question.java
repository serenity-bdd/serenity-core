package net.serenitybdd.screenplay;

import net.serenitybdd.screenplay.questions.QuestionWithDefinedSubject;

public interface Question<ANSWER> {
    ANSWER answeredBy(Actor actor);
    default String getSubject() { return ""; }

    static QuestionBuilder about(String subject) {
        return new QuestionBuilder(subject);
    }

    class QuestionBuilder {
        private final String subject;

        QuestionBuilder(String subject) {
            this.subject = subject;
        }

        public <T> Question<T> answeredBy(Question<T> questionToAsk) {
            return new QuestionWithDefinedSubject(questionToAsk, subject);
        }

    }
}
