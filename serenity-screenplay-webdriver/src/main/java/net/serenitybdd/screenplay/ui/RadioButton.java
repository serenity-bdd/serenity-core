package net.serenitybdd.screenplay.ui;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static net.serenitybdd.screenplay.ui.LocatorStrategies.fieldWithLabel;


/**
 * An HTML Radio button
 */
public class RadioButton {

    private static final String BY_ID_OR_NAME = "css:input[id='{0}' i],input[name='{0}' i],input[data-test='{0}' i],input[class*='{0}'],[aria-label='{0}' i]";
    private static final String BY_VALUE = "css:input[type='radio'][value='{0}' i]";

    /**
     * Locate a radio button with a given name or id.
     */
    public static Target called(String name) {
        return Target.the("the '" + name + "' radio button").locatedBy(BY_ID_OR_NAME).of(name);
    }

    /**
     * Locate a radio button with a given value.
     */
    public static Target withValue(String value) {
        return Target.the("the '" + value + "' radio button").locatedBy(BY_VALUE).of(value);
    }

    /**
     * Locate a radio button with a given label
     */
    public static Target withLabel(String labelText) {
        return Target.the("the '" + labelText + " radio button").locatedBy(fieldWithLabel(labelText));
    }
}