package net.serenitybdd.screenplay.conditions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class ConditionalPerformableOnQuestion extends ConditionalPerformable {
    private final Question<Boolean> condition;

    public ConditionalPerformableOnQuestion(Question<Boolean> condition) {
        super();
        this.condition = condition;
    }

    @Override
    protected Boolean evaluatedConditionFor(Actor actor) {
        return condition.answeredBy(actor);
    }
}
