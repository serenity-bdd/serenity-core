package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.targets.SearchableTarget;
import net.serenitybdd.screenplay.targets.Target;

import static net.serenitybdd.core.pages.RenderedPageObjectView.containingTextAndMatchingCSS;

/**
 * An HTML element representing a button.
 */
public class PageElement {

    private static final String BY_ID_NAME_OR_CLASS = "css:[id='{0}' i],[name='{0}' i],[data-test='{0}' i],[class*='{0}']";
    private static final String STRICTLY_CONTAINS_TEXT = "xpath:.//*[contains(text(),'{0}')]";

    /**
     * Locate an element with a given name.
     */
    public static SearchableTarget called(String name) {
        return Target.the("the '" + name + "' element").locatedBy(BY_ID_NAME_OR_CLASS).of(name);
    }

    /**
     * Locate an element that contains a specified text value in it's body.
     * This will not include text that is contained in nested elements.
     */
    public static SearchableTarget containingText(String text) {
        return Target.the("the element containing text '" + text + "'").locatedBy(STRICTLY_CONTAINS_TEXT).of(text);
    }

    /**
     * Look for an element matching a given CSS or XPath expression that contains a given text.
     */
    public static Target containingText(String cssOrXPathLocator, String text) {
        return Target.the("the element containing text '" + text + "'")
                        .locatedBy(containingTextAndMatchingCSS(cssOrXPathLocator, text));
    }


    public static PageElementBuilder locatedBy(String selector) {
        return new PageElementBuilder(selector);
    }

}