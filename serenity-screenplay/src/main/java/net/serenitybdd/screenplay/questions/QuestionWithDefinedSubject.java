package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class QuestionWithDefinedSubject<T> implements Question<T> {

    private final Question<T> theQuestion;
    private final String subject;

    public QuestionWithDefinedSubject(Question<T> theQuestion, String subject) {
        this.theQuestion = theQuestion;
        this.subject = subject;
    }

    @Override
    public T answeredBy(Actor actor) {
        return theQuestion.answeredBy(actor);
    }

    @Override
    public String getSubject() {
        return subject;
    }
}