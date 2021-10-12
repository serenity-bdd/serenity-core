package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.actions.SelectFromOptions;
import net.serenitybdd.screenplay.targets.Target;

import static net.serenitybdd.screenplay.ui.LocatorStrategies.fieldWithLabel;

/**
 * Interact with a checkbox element
 */
public class Checkbox {

    private static final String BY_ID_OR_NAME = "css:input[id='{0}' i],input[name='{0}' i],input[data-test='{0}' i]";
    private static final String BY_VALUE = "css:input[type='checkbox'][value='{0}' i]";

    /**
     * Locate a radio button with a given name or id.
     */
    public static Target called(String name) {
        return Target.the("'" + name + "' checkbox").locatedBy(BY_ID_OR_NAME).of(name);
    }

    /**
     * Locate a radio button with a given value.
     */
    public static Target withValue(String value) {
        return Target.the("'" + value + "' checkbox").locatedBy(BY_VALUE).of(value);
    }

    /**
     * Locate a radio button with a given label
     */
    public static Target withLabel(String labelText) {
        return Target.the(labelText + " checkbox").locatedBy(fieldWithLabel(labelText));
    }}
