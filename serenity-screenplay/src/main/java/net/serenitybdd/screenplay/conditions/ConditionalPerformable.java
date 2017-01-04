package net.serenitybdd.screenplay.conditions;

import com.google.common.collect.Maps;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;

import java.util.Map;

public abstract class ConditionalPerformable implements Performable {

    protected final Map<Boolean, Performable[]> outcomeToPerform;

    public ConditionalPerformable() {
        this.outcomeToPerform = Maps.newHashMap();
        outcomeToPerform.put(true, new Performable[]{ new SilentPerformable() });
        outcomeToPerform.put(false, new Performable[]{ new SilentPerformable() });
    }

    public ConditionalPerformable andIfSo(Performable... taskToPerform) {
        outcomeToPerform.put(true, taskToPerform);
        return this;
    }

    public ConditionalPerformable otherwise(Performable... taskToPerform) {
        outcomeToPerform.put(false, taskToPerform);
        return this;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(outcomeToPerform.get(evaluatedConditionFor(actor)));
    }

    protected abstract Boolean evaluatedConditionFor(Actor actor);
}
