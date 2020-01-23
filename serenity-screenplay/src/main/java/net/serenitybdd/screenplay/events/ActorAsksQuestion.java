package net.serenitybdd.screenplay.events;

import net.serenitybdd.screenplay.Question;

public class ActorAsksQuestion extends ActorPerformanceEvent {
    private final Question question;

    public ActorAsksQuestion(Question question, String actor) {
        super(actor);
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }
}
