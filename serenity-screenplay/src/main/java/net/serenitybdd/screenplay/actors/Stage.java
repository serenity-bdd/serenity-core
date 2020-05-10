package net.serenitybdd.screenplay.actors;

import com.google.common.base.Preconditions;
import net.serenitybdd.screenplay.Actor;

import java.util.Optional;

public class Stage {

    private Actor actorInTheSpotlight;
    private final Cast cast;

    public Stage(Cast cast) {
        this.cast = cast;
    }

    public Actor shineSpotlightOn(String actorName) {

        Optional<Actor> knownActor = cast.getActors()
                                         .stream()
                                         .filter(actor -> actor.getName().equalsIgnoreCase(actorName))
                                         .findFirst();

        actorInTheSpotlight = knownActor.orElseGet(() -> cast.actorNamed(actorName));
        return theActorInTheSpotlight().withNoPronoun();
    }

    public Actor theActorInTheSpotlight() {
        Preconditions.checkNotNull(actorInTheSpotlight);
        return actorInTheSpotlight;
    }

    /**
     * A shortened form of theActorInTheSpotight()
     */
    public Actor theActor() {
        return theActorInTheSpotlight();
    }

    public void drawTheCurtain() {
        cast.dismissAll();
    }

    public boolean anActorIsOnStage() {
        return actorInTheSpotlight != null;
    }
}
