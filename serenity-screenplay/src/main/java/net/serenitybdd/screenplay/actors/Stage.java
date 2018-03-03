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

    public void drawTheCurtain() {
        cast.dismissAll();
    }
}
