package net.serenitybdd.screenplay.formatting;

import net.serenitybdd.screenplay.Consequence;
import net.thucydides.core.reports.html.Formatter;

public class FormattedTitle {
    public static <T> String ofConsequence(Consequence<T> consequence) {
        return Formatter.htmlAttributeCompatible(consequence.toString());
    }
}
