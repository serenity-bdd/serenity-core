package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.actions.DeselectFromOptions;

/**
 * Interact with a SELECT Html element
 */
public class Deselect {
    public static DeselectFromOptions option(String option) {
        return DeselectFromOptions.byVisibleText(option);
    }

    public static DeselectFromOptions value(String volvo) {
        return DeselectFromOptions.byValue(volvo);
    }

    public static DeselectFromOptions optionNumber(int index) {
        return DeselectFromOptions.byIndex(index);
    }
}
