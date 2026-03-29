package net.thucydides.core.steps.events;

public class AssignAbilityToActorEvent extends StepEventBusEventBase {

    private final String actorName;
    private final String ability;

    public AssignAbilityToActorEvent(String actorName, String ability) {
        this.actorName = actorName;
        this.ability = ability;
    }

    @Override
    public void play() {
        getStepEventBus().getBaseStepListener().latestTestOutcome().ifPresent(
                testOutcome -> testOutcome.assignAbility(actorName, ability)
        );
    }
}
