package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionWithDefinedSubject<T> implements Question<T> {

    private final Question<T> theQuestion;
    private final String subject;
    private final List<Performable> predecingTasks;

    public QuestionWithDefinedSubject(Question<T> theQuestion, String subject, List<Performable> predecingTasks) {
        this.theQuestion = theQuestion;
        this.subject = subject;
        this.predecingTasks = predecingTasks;
    }

    public QuestionWithDefinedSubject(Question<T> theQuestion, String subject) {
        this(theQuestion,subject, new ArrayList<>());
    }
    @Override
    public T answeredBy(Actor actor) {
        actor.attemptsTo(predecingTasks.toArray(new Performable[]{}));
        return theQuestion.answeredBy(actor);
    }

    @Override
    public String getSubject() {
        return subject;
    }
}