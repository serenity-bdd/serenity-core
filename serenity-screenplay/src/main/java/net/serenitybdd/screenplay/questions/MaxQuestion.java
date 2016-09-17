package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

@Subject("the maximum value of #listQuestion")
public class MaxQuestion<T extends Object & Comparable<? super T>> implements Question<T> {

    private final Question<? extends Collection<T>> listQuestion;
    private final Comparator<? super T> comparator;

    public MaxQuestion(Question<? extends Collection<T>> listQuestion) {
        this(listQuestion, null);
    }

    public MaxQuestion(Question<? extends Collection<T>> listQuestion, Comparator<? super T> comparator) {
        this.listQuestion = listQuestion;
        this.comparator = comparator;
    }

    @Override
    public T answeredBy(Actor actor) {
        return Collections.max(listQuestion.answeredBy(actor), comparator);
    }
}