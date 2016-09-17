package net.serenitybdd.screenplay.questions;

import com.google.common.collect.Lists;
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
        return Lists.reverse(listQuestion.answeredBy(actor));
    }
}