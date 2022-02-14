package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.targets.SearchableTarget;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.ui.LocatorStrategies.fieldWithLabel;

/**
 * An HTML text area field
 */
public class TextArea {

    private static final String BY_ID_OR_NAME = "css:textarea[id='{0}' i],textarea[name='{0}' i],textarea[data-test='{0}' i],[aria-label='{0}' i]";
    private static final String ARIA_LABEL = "textarea[aria-label='{0}' i]";

    /**
     * Locate a field with a given name, id, data-test or ARIA label.
     */
    public static SearchableTarget withNameOrId(String name) {
        return Target.the("the '" + name + "' text area").locatedBy(BY_ID_OR_NAME).of(name);
    }

    /**
     * Locate an HTML input field with a specified placeholder name
     */
    public static SearchableTarget withPlaceholder(String placeholderName) {
        String placeholderAttribute = CSSAttributeValue.withEscapedQuotes(placeholderName);
        return Target.the("'" + placeholderName + "' field")
                .locatedBy("css:[placeholder='{0}']")
                .of(placeholderAttribute);
    }

    public static Target withLabel(String labelText) {
        return Target.the(labelText + " text area").locatedBy(fieldWithLabel(labelText));
    }

    /**
     * Locate a button using the ARIA label value
     */
    public static SearchableTarget withAriaLabel(String name) {
        return Target.the("'" + name + "' text area").locatedByFirstMatching("css:" + ARIA_LABEL).of(name);
    }

    /**
     * Look for an element with a given CSS class
     */
    public static SearchableTarget withCSSClass(String className) {
        return TargetFactory.forElementOfType("text area").withCSSClass(className);
    }

    /**
     * Locate a button using an arbitrary CSS or XPath expression
     */
    public static SearchableTarget locatedBy(String selector) {
        return TargetFactory.forElementOfType("text area").locatedByXPathOrCss(selector);
    }

    /**
     * Locate a button using an arbitrary CSS or XPath expression
     */
    public static SearchableTarget located(By selector) {
        return TargetFactory.forElementOfType("text area").locatedBy(selector);
    }

}