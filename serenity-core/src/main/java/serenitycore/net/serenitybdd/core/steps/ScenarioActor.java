package serenitycore.net.serenitybdd.core.steps;

public class ScenarioActor {
    /**
     * The name of the actor, automatically injected by Serenity based on the name of the step variable.
     */
    String actor;

    public void isCalled(String actorName) {
        this.actor = actorName;
    }

    public String getActorName() {
        return actor;
    }

    @Override
    public String toString() {
        return actor;
    }
}
