package net.serenitybdd.screenplay.actors;

import com.google.common.base.Splitter;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.List;

/**
 * The stage is used to keep track of the actors taking part in a Screenplay test.
 * It is useful if you don't keep track of the actors explicitly, but just refer to them by name, as is often done
 * in Cucumber scenarios.
 *
 * Actors can be referred to by name (which must be unique for a given actor) or a pronoun.
 * The default pronouns are "he","she","they" and "it", and they are used interchangeably - any pronoun will always
 * refer to last named actor who performed some action.
 * Pronouns can be configured using the screenplay.pronouns property, e.g.
 * <pre>
 *     <code>
 *         screenplay.pronouns = il,elle
 *     </code>
 * </pre>
 *
 * The current stage is kept as a ThreadLocal object, so if you have multiple threads in the same Screenplay test,
 * you need to propagate the stage to each new thread using the setTheStage() method.
 */
public class OnStage {

    private final static String DEFAULT_PRONOUNS = "he,she,they,it,his,her,their,its";
    private final static String A_NEW_ACTOR = "An actor";

    private static final ThreadLocal<Stage> STAGE = new ThreadLocal<>();

    /**
     * Set the stage before calling the actors
     */
    public static Stage setTheStage(Cast cast) {
        STAGE.set(new Stage(cast));
        return stage();
    }

    /**
     * Check whether the stage has already been set.
     */
    public static boolean theStageIsSet() {
        return STAGE.get() != null;
    }

    /**
     * Set the stage to a specific stage object.
     * This is rarely needed but sometimes comes in handy when running tasks in parallel.
     */
    public static Stage setTheStage(Stage stage) {
        STAGE.set(stage);
        return stage();
    }

    /**
     * Returns an actor with a given name, creating a new actor if the actor is not already on stage.
     * If a pronoun is used (e.g "she creates a new account") then the current actor in the spotlight will be used.
     */
    public static Actor theActorCalled(String requiredActor) {
        boolean theActorIsReferredToByAPronoun = pronouns().stream().anyMatch(pronoun -> pronoun.equalsIgnoreCase(requiredActor));

        if (theActorIsReferredToByAPronoun) {
            return stage().theActorInTheSpotlight().usingPronoun(requiredActor);
        }
        if (anActorIsOnStage() && theActorInTheSpotlight().getName().equals(A_NEW_ACTOR)) {
            theActorInTheSpotlight().assignName(requiredActor);
            return theActorInTheSpotlight();
        }
        
        return stage().shineSpotlightOn(requiredActor);
    }

    private static boolean anActorIsOnStage() {
        return stage().anActorIsOnStage();
    }

    /**
     * Create a new actor whose name is not yet known.
     * The next time the theActorCalled() method is used, this name will be assigned to this actor.
     */
    public static Actor aNewActor() {
        return stage().shineSpotlightOn(A_NEW_ACTOR);
    }

    /**
     * A shorter version of "theActorCalled()"
     */
    public static Actor theActor(String actorName) {
        return theActorCalled(actorName);
    }

    /**
     * The actor in the spotlight is the last actor on the stage who has performed any activity.
     */
    public static Actor theActorInTheSpotlight() {
        return stage().theActorInTheSpotlight();
    }

    /**
     * A shorter version of "theActorInTheSpotlight().attemptsTo(...)"
     */
    public static void withCurrentActor(Performable... performTasks) {
        theActorInTheSpotlight().attemptsTo(performTasks);
    }

    /**
     * Get the current stage. Rarely needed for non-internal use, except when running tasks in parallel.
     * In that case, you will need to call OnStage.setTheStage(stage) in each parallel thread if you use
     * OnStage methods such as theActorInTheSpotlight()
     */
    public static Stage stage() {
        if (STAGE.get() == null) {
            throw new NoStageException("No stage available - it looks like you haven't called the setTheStage() method before calling this one.");
        } else {
            return STAGE.get();
        }
    }

    /**
     * Perform any cleanup actions on each actor on the stage.
     * This calls the `wrapUp()` method if defined on each actor on the stage.
     */
    public static void drawTheCurtain() {
        if (STAGE.get() != null) {
            stage().drawTheCurtain();
        }
    }

    private static List<String> pronouns() {
        EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);

        return Splitter.on(",").trimResults().omitEmptyStrings().splitToList(
                ThucydidesSystemProperty.SCREENPLAY_PRONOUNS.from(environmentVariables, DEFAULT_PRONOUNS)
        );
    }
}
