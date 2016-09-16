package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;

import java.util.Collection;
import java.util.Collections;

@Subject("the maximum value of #listQuestion")
public class MaxQuestion<T extends Object & Comparable<? super T>> implements Question<T> {

    private final Question<Collection<T>> listQuestion;

    public MaxQuestion(Question<Collection<T>> listQuestion) {
        this.listQuestion = listQuestion;
    }

    @Override
    public T answeredBy(Actor actor) {
        return Collections.max(listQuestion.answeredBy(actor));
    }
}