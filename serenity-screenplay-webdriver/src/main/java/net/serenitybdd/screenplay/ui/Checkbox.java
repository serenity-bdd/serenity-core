package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.targets.SearchableTarget;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.ui.LocatorStrategies.fieldWithLabel;

/**
 * Interact with a checkbox element
 * This element will match a checkbox field with a matching value, id, class or data-test attribute.
 */
public class Checkbox {

    private static final String BY_ID_NAME_CLASS_OR_DATA_TEST = "css:input[type='checkbox'][name='{0}' i],input[type='checkbox'][id='{0}' i],input[type='checkbox'][data-test='{0}' i],[aria-label='{0}' i]";
    private static final String BY_ARIA_LABEL= "css:[aria-label='{0}' i]";
    private static final String BY_VALUE = "css:input[type='checkbox'][value='{0}' i]";

    /**
     * Locate a radio button with a given name or id.
     */
    public static SearchableTarget withNameOrId(String name) {
        return Target.the("'" + name + "' checkbox").locatedBy(BY_ID_NAME_CLASS_OR_DATA_TEST).of(name);
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
    }

    /**
     * Locate a radio button with a given ARIA label
     */
    public static Target withAriaLabel(String labelText) {
        return Target.the(labelText + " checkbox").locatedBy(BY_ARIA_LABEL).of(labelText);
    }

    /**
     * Locate an checkbox using an arbitrary CSS or XPath expression
     */
    public static SearchableTarget locatedBy(String selector) {
        return TargetFactory.forElementOfType("checkbox").locatedByXPathOrCss(selector);
    }
    public static SearchableTarget located(By selector) {
        return TargetFactory.forElementOfType("checkbox").locatedBy(selector);
    }

}
