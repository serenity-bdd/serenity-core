package net.serenitybdd.screenplay.actors;

import com.google.common.base.Preconditions;
import net.serenitybdd.screenplay.Actor;

public class Stage {

    private Actor actorInTheSpotlight;
    private final Cast cast;

    public Stage(Cast cast) {
        this.cast = cast;
    }

    public Actor shineSpotlightOn(String actorName) {
        Actor star = cast.actorNamed(actorName);
        actorInTheSpotlight = star;
        return theActorInTheSpotlight();
    }

    public Actor theActorInTheSpotlight() {
        Preconditions.checkNotNull(actorInTheSpotlight);
        return actorInTheSpotlight;
    }

    public void drawTheCurtain() {
        cast.dismissAll();
    }
}
