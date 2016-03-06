package net.serenitybdd.screenplay.events;

import net.serenitybdd.screenplay.Question;

public class ActorAsksQuestion {
    private final Question question;

    public ActorAsksQuestion(Question question) {
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }
}
