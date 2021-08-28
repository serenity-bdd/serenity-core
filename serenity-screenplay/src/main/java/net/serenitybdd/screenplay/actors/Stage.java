package net.serenitybdd.screenplay.actors;

import com.google.common.base.Preconditions;
import net.serenitybdd.screenplay.Actor;

import java.util.Optional;

/**
 * The stage keeps track of the actors currently in a screenplay test, referenced by name.
 * You rarely need to use this class directly in your tests. Normally you would use the OnStage class instead.
 */
public class Stage {

    private Actor actorInTheSpotlight;
    private final Cast cast;

    public Stage(Cast cast) {
        this.cast = cast;
    }

    /**
     * Place an actor with a given name in the spotlight, without the intent to have them perform an action at this time.
     */
    public Actor shineSpotlightOn(String actorName) {

        Optional<Actor> knownActor = cast.getActors()
                                         .stream()
                                         .filter(actor -> actor.getName().equalsIgnoreCase(actorName))
                                         .findFirst();

        actorInTheSpotlight = knownActor.orElseGet(() -> cast.actorNamed(actorName));
        return theActorInTheSpotlight().withNoPronoun();
    }

    /**
     * Return the current actor in the spotlight.
     */
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

    /**
     * This method should be called at the end of the screenplay test to perform teardown actions on each actor.
     * It will generally be done automatically by Serenity.
     */
    public void drawTheCurtain() {
        cast.dismissAll();
    }

    /**
     * Check whether there is any actor in the spotlight at the moment.
     */
    public boolean anActorIsOnStage() {
        return actorInTheSpotlight != null;
    }
}
