package net.serenitybdd.screenplay.actors;

import net.serenitybdd.screenplay.Actor;

public class OnStage {

    private static final ThreadLocal<Stage> stage = new ThreadLocal<>();

    /**
     * Set the stage before calling the actors
     */
    public static Stage setTheStage(Cast cast) {
        stage.set(new Stage(cast));
        return stage();
    }

    public static Actor theActorCalled(String requiredActor) {
        return stage().shineSpotlightOn(requiredActor);
    }

    public static Actor theActorInTheSpotlight() {
        return stage().theActorInTheSpotlight();
    }

    private static Stage stage() { return stage.get(); }

    public static void drawTheCurtain() {
        if (stage() != null) {
            stage().drawTheCurtain();
        }
    }
}
