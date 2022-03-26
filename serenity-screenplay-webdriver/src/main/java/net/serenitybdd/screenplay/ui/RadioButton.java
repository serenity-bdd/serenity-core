package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.targets.SearchableTarget;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.ui.LocatorStrategies.fieldWithLabel;

/**
 * An HTML Radio button
 */
public class RadioButton {

    private static final String BY_ID = "css:input[id='{0}' i],input[data-test='{0}' i],[aria-label='{0}' i]";
    private static final String BY_NAME = "css:input[name='{0}' i]";
    private static final String BY_VALUE = "css:input[type='radio'][value='{0}' i]";
    private static final String ARIA_LABEL = "[aria-label='{0}' i]";

    /**
     * Locate a radio button with a given name or id.
     */
    public static SearchableTarget withId(String id) {
        return Target.the("the '" + id + "' radio button").locatedBy(BY_ID).of(id);
    }

    /**
     * Locate a radio button with a given name
     */
    public static SearchableTarget withName(String name) {
        return Target.the("the '" + name + "' radio button").locatedBy(BY_NAME).of(name);
    }

    /**
     * Locate a radio button with a given value.
     */
    public static SearchableTarget withValue(String value) {
        return Target.the("the '" + value + "' radio button").locatedBy(BY_VALUE).of(value);
    }

    /**
     * Locate a radio button with a given label
     */
    public static SearchableTarget withLabel(String labelText) {
        return Target.the("the '" + labelText + " radio button").locatedBy(fieldWithLabel(labelText));
    }

    /**
     * Locate a button using the ARIA label value
     */
    public static SearchableTarget withAriaLabel(String name) {
        return Target.the("'" + name + "' radio button").locatedByFirstMatching("css:" + ARIA_LABEL).of(name);
    }
    /**
     * Look for an element with a given CSS class
     */
    public static SearchableTarget withCSSClass(String className) {
        return TargetFactory.forElementOfType("radio button").withCSSClass(className);
    }

    /**
     * Locate a button using an arbitrary CSS or XPath expression
     */
    public static SearchableTarget locatedBy(String selector) {
        return TargetFactory.forElementOfType("radio button").locatedByXPathOrCss(selector);
    }

    /**
     * Locate a button using an arbitrary CSS or XPath expression
     */
    public static SearchableTarget located(By selector) {
        return TargetFactory.forElementOfType("radio button").locatedBy(selector);
    }

}