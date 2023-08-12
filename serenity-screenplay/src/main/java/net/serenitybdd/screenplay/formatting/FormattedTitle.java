package net.serenitybdd.screenplay.formatting;

import net.serenitybdd.core.di.SerenityInfrastructure;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Consequence;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.serenitybdd.core.di.SerenityInfrastructure;
import net.thucydides.core.reports.html.Formatter;
import net.thucydides.model.util.EnvironmentVariables;

public class FormattedTitle {

    private final EnvironmentVariables environmentVariables;
    private final Actor actor;

    public FormattedTitle(EnvironmentVariables environmentVariables, Actor actor) {
        this.environmentVariables = environmentVariables;
        this.actor = actor;
    }

    public static <T> String ofConsequence(Consequence<T> consequence, Actor actor) {
        return new FormattedTitle(SystemEnvironmentVariables.currentEnvironmentVariables(), actor).getFormattedTitleFor(consequence);
    }

    public <T> String getFormattedTitleFor(Consequence<T> consequence) {
        Formatter formatter = SerenityInfrastructure.getFormatter();

        String prefix = "";
        if (ThucydidesSystemProperty.SERENITY_INCLUDE_ACTOR_NAME_IN_CONSEQUENCES.booleanFrom(environmentVariables, false)) {
            prefix = "For " + actor.getName() + ": ";
        }

        return prefix + formatter.htmlAttributeCompatible(consequence.toString());
    }
}
