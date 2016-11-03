package net.serenitybdd.screenplay.formatting;

import net.serenitybdd.screenplay.Consequence;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.reports.html.Formatter;

public class FormattedTitle {
    public static <T> String ofConsequence(Consequence<T> consequence) {
        Formatter formatter = Injectors.getInjector().getInstance(Formatter.class);
        return formatter.htmlAttributeCompatible(consequence.toString());
    }
}
