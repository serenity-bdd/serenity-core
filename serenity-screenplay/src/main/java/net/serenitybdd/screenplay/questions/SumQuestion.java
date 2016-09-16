package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;

import java.util.Collection;

@Subject("the sum of #listQuestion")
public class SumQuestion implements Question<Integer> {

    private final Question<Collection<Integer>> listQuestion;

    public SumQuestion(Question<Collection<Integer>> listQuestion) {
        this.listQuestion = listQuestion;
    }

    @Override
    public Integer answeredBy(Actor actor) {
        Integer total = 0;
        for (Integer number : listQuestion.answeredBy(actor)) {
            total = total + number;
        }
        return total;
    }
}