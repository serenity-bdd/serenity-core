package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.DeselectFromOptions;
import net.serenitybdd.screenplay.actions.deselectactions.DeselectAllOptions;
import net.serenitybdd.screenplay.targets.Target;

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

    public static Performable allOptionsFrom(Target target) {
        return new DeselectAllOptions(target);
    }
}
