package net.serenitybdd.screenplay.actors;

import com.google.common.base.Splitter;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.List;
import java.util.Optional;

public class OnStage {

    private final static String DEFAULT_PRONOUNS = "he,she,they,it";
    private final static String A_NEW_ACTOR = "An actor";

    private static final ThreadLocal<Stage> stage = new ThreadLocal<>();


    /**
     * Set the stage before calling the actors
     */
    public static Stage setTheStage(Cast cast) {
        stage.set(new Stage(cast));
        return stage();
    }

    public static Actor theActorCalled(String requiredActor) {
        if (pronouns().contains(requiredActor)) {
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

    public static Actor aNewActor() {
        return stage().shineSpotlightOn(A_NEW_ACTOR);
    }


    /**
     * A shorter version of "theActorCalled()"
     */
    public static Actor theActor(String actorName) {
        return theActorCalled(actorName);
    }

    public static Actor theActorInTheSpotlight() {
        return stage().theActorInTheSpotlight();
    }

    /**
     * A shorter version of "theActorInTheSpotlight().attemptsTo(...)"
     */
    public static void withCurrentActor(Performable... performTasks) {
        theActorInTheSpotlight().attemptsTo(performTasks);
    }

    private static Stage stage() {
        return Optional.ofNullable(stage.get())
                .orElseThrow(() -> new NoStageException("No stage available - it looks like you haven't called the setTheStage() method before calling this one."));
    }

    public static void drawTheCurtain() {
        if (stage() != null) {
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
