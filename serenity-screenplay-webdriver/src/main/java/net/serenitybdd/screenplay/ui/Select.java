package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.DeselectFromOptions;
import net.serenitybdd.screenplay.actions.SelectFromOptions;
import net.serenitybdd.screenplay.targets.Target;

/**
 * Interact with a SELECT Html element
 */
public class Select {
    public static SelectFromOptions option(String option) {
        return SelectFromOptions.byVisibleText(option);
    }

    public static SelectFromOptions options(String... options) {
        return SelectFromOptions.byVisibleText(options);
    }

    public static SelectFromOptions value(String value) {
        return SelectFromOptions.byValue(value);
    }

    public static SelectFromOptions values(String... values) {
        return SelectFromOptions.byValue(values);
    }

    public static SelectFromOptions optionNumber(int index) {
        return SelectFromOptions.byIndex(index);
    }

    public static SelectFromOptions optionNumbers(Integer... indexes) {
        return SelectFromOptions.byIndex(indexes);
    }

    public static Performable noOptionsIn(Target dropdownList) {
        return DeselectFromOptions.clear(dropdownList);
    }
}
