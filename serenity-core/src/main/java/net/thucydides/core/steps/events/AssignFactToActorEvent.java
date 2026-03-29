package net.thucydides.core.steps.events;

public class AssignFactToActorEvent extends StepEventBusEventBase {

    private final String actorName;
    private final String fact;

    public AssignFactToActorEvent(String actorName, String fact) {
        this.actorName = actorName;
        this.fact = fact;
    }

    @Override
    public void play() {
        getStepEventBus().getBaseStepListener().latestTestOutcome().ifPresent(
                testOutcome -> testOutcome.assignFact(actorName, fact)
        );
    }
}
