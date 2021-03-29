package serenityscreenplay.net.serenitybdd.screenplay.formatting;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Consequence;
import serenitymodel.net.thucydides.core.ThucydidesSystemProperty;
import serenitymodel.net.thucydides.core.guice.Injectors;
import net.thucydides.core.reports.html.Formatter;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;

public class FormattedTitle {

    private EnvironmentVariables environmentVariables;
    private Actor actor;

    public FormattedTitle(EnvironmentVariables environmentVariables, Actor actor) {
        this.environmentVariables = environmentVariables;
        this.actor = actor;
    }

    public static <T> String ofConsequence(Consequence<T> consequence, Actor actor) {
        return new FormattedTitle(Injectors.getInjector().getInstance(EnvironmentVariables.class), actor).getFormattedTitleFor(consequence);
    }

    public <T> String getFormattedTitleFor(Consequence<T> consequence) {
        Formatter formatter = Injectors.getInjector().getInstance(Formatter.class);

        String prefix = "";
        if (ThucydidesSystemProperty.SERENITY_INCLUDE_ACTOR_NAME_IN_CONSEQUENCES.booleanFrom(environmentVariables, false)) {
            prefix = "For " + actor.getName() + ": ";
        }

        return prefix + formatter.htmlAttributeCompatible(consequence.toString());
    }
}
