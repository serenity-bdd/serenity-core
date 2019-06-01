package net.serenitybdd.screenplay.formatting;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Consequence;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.reports.html.Formatter;
import net.thucydides.core.util.EnvironmentVariables;

public class FormattedTitle {


    public static <T> String ofConsequence(Consequence<T> consequence, Actor actor) {
        Formatter formatter = Injectors.getInjector().getInstance(Formatter.class);
        EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);

        String prefix = "";
        if (ThucydidesSystemProperty.SERENITY_INCLUDE_ACTOR_NAME_IN_CONSEQUENCES.booleanFrom(environmentVariables, false)) {
            prefix = "For " + actor.getName() + ": ";
        }

        return prefix + formatter.htmlAttributeCompatible(consequence.toString());
    }
}
