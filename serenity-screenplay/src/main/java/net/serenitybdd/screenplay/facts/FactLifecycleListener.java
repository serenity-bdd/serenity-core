package net.serenitybdd.screenplay.facts;

import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.steps.Droppable;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.steps.StepListenerAdapter;

import java.time.ZonedDateTime;

public class FactLifecycleListener extends StepListenerAdapter implements Droppable {
    private final Actor actor;
    private final Fact fact;

    public FactLifecycleListener(Actor actor, Fact fact) {
        this.fact = fact;
        this.actor = actor;
    }

    @Override
    public void testFinished(TestOutcome result) {
        fact.teardown(actor);
    }

    @Override
    public void testFinished(TestOutcome result, boolean isInDataDrivenTest, ZonedDateTime finishTime) {
        fact.teardown(actor);
    }
}
