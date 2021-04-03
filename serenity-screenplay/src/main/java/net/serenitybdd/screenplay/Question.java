package net.serenitybdd.screenplay;

public interface Question<ANSWER> {
    ANSWER answeredBy(Actor actor);
    default String getSubject() { return ""; }

    static QuestionBuilder about(String subject) {
        return new QuestionBuilder(subject);
    }


}
