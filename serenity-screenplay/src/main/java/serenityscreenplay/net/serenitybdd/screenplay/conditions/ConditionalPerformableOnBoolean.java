package serenityscreenplay.net.serenitybdd.screenplay.conditions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;

public class ConditionalPerformableOnBoolean extends ConditionalPerformable {
    private final Boolean condition;

    public ConditionalPerformableOnBoolean(Boolean condition) {
        super();
        this.condition = condition;
    }

    @Override
    protected Boolean evaluatedConditionFor(Actor actor) {
        return condition;
    }
}
