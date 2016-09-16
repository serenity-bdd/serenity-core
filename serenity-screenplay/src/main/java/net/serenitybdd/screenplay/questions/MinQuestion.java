package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;

import java.util.Collection;
import java.util.Collections;

@Subject("the minimum value of #listQuestion")
public class MinQuestion<T extends Object & Comparable<? super T>> implements Question<T> {

    private final Question<Collection<T>> listQuestion;

    public MinQuestion(Question<Collection<T>> listQuestion) {
        this.listQuestion = listQuestion;
    }

    @Override
    public T answeredBy(Actor actor) {
        return Collections.min(listQuestion.answeredBy(actor));
    }
}