package net.serenitybdd.screenplay.formatting;

import net.serenitybdd.model.collect.NewList;

import java.util.List;

public class StripRedundantTerms {

    private final static List<String> REDUNDANT_HAMCREST_PREFIXES = NewList.of("is ","be ","should be");

    public static String from(String expression) {
        for (String prefix : REDUNDANT_HAMCREST_PREFIXES) {
            expression = removePrefix(prefix, expression);
        }
        return expression;
    }

    private static String removePrefix(String prefix, String expression) {
        if (expression.startsWith(prefix)) {
            expression = expression.substring(3);
        }
        return expression;
    }
}
