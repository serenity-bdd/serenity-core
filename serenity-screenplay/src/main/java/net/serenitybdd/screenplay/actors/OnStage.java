package net.serenitybdd.screenplay.actors;

import com.google.common.base.Splitter;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.List;

public class OnStage {

    private final static String DEFAULT_PRONOUNS = "he,she,they";

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
        
        return stage().shineSpotlightOn(requiredActor);
    }

    public static Actor theActorInTheSpotlight() {
        return stage().theActorInTheSpotlight();
    }

    private static Stage stage() {
        return stage.get();
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
