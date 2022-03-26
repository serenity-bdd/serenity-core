package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.targets.SearchableTarget;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.ui.LocatorStrategies.containingTextAndMatchingCSS;
import static net.serenitybdd.screenplay.ui.LocatorStrategies.containingTextAndMatchingCSSIgnoringCase;

/**
 * An HTML element representing any HTML element.
 */
public class PageElement {

    private static final String BY_NAME_ID_OR_ARIA_LABEL = "css:[id='{0}' i],[name='{0}' i],[data-test='{0}' i],[aria-label='{0}' i]";

    /**
     * Locate an element with a given name.
     */
    public static SearchableTarget withNameOrId(String name) {
        return Target.the("the '" + name + "' element").locatedBy(BY_NAME_ID_OR_ARIA_LABEL).of(name);
    }

    /**
     * Look for an element matching a given CSS or XPath expression that contains a given text.
     */
    public static SearchableTarget containingText(String cssOrXPathLocator, String text) {
        return Target.the("the element containing text '" + text + "'")
                .locatedBy(containingTextAndMatchingCSS(cssOrXPathLocator, text));
    }

    /**
     * Look for an element matching a given CSS or XPath expression that contains a given text regardless of case
     */
    public static SearchableTarget containingTextIgnoringCase(String cssOrXPathLocator, String text) {
        return Target.the("the element containing text '" + text + "'")
                .locatedBy(containingTextAndMatchingCSSIgnoringCase(cssOrXPathLocator, text));
    }

    /**
     * Look for an element with a given CSS class
     */
    public static SearchableTarget withCSSClass(String className) {
        return TargetFactory.forElementOfType("element").withCSSClass(className);
    }

    /**
     * Locate an element that contains a specified text value in it's body.
     * This will not include text that is contained in nested elements.
     */
    public static SearchableTarget containingText(String text) {
        return TargetFactory.forElementOfType("element").containingText(text);
    }

    /**
     * Locate an element using an arbitrary CSS or XPath expression
     */
    public static SearchableTarget locatedBy(String selector) {
        return TargetFactory.forElementOfType("element").locatedByXPathOrCss(selector);
    }

    /**
     * Locate an element using a By locator
     */
    public static SearchableTarget located(By selector) {
        return TargetFactory.forElementOfType("element").locatedBy(selector);
    }
}
