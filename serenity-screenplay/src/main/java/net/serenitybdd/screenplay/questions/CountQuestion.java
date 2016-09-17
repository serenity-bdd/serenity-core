package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;

import java.util.Collection;

@Subject("the total number of #listQuestion")
public class CountQuestion implements Question<Integer> {

    private final Question<? extends Collection> listQuestion;

    public CountQuestion(Question<? extends Collection> listQuestion) {
        this.listQuestion = listQuestion;
    }

    @Override
    public Integer answeredBy(Actor actor) {
        return listQuestion.answeredBy(actor).size();
    }
}
