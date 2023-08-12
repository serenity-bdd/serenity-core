package net.serenitybdd.screenplay.questions;

import net.serenitybdd.model.collect.NewList;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;

import java.util.List;

@Subject("the minimum value of #listQuestion")
public class ReverseQuestion<T> implements Question<List<T>> {

    private final Question<? extends List<T>> listQuestion;

    public ReverseQuestion(Question<? extends List<T>> listQuestion) {
        this.listQuestion = listQuestion;
    }

    @Override
    public List<T> answeredBy(Actor actor) {
        return NewList.reverse(listQuestion.answeredBy(actor));
    }
}
