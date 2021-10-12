package net.serenitybdd.screenplay.ui;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An HTML element representing a button.
 */
public class PageElement {

    private static final String BY_ID_OR_NAME = "css:[id='{0}' i],[name='{0}' i],[data-test='{0}' i]";
    private static final String STRICTLY_CONTAINS_TEXT = "xpath:.//*[contains(text(),'{0}')]";

    /**
     * Locate an element with a given name.
     */
    public static Target called(String name) {
        return Target.the("'" + name + "' element").locatedByFirstMatching(BY_ID_OR_NAME).of(name);
    }

    /**
     * Locate an element that contains a specified text value in it's body.
     * This will not include text that is contained in nested elements.
     */
    public static Target containingText(String text) {
        return Target.the("Element containing text '" + text + "'").locatedByFirstMatching(STRICTLY_CONTAINS_TEXT).of(text);
    }

    /**
     * Look for an element matching a given CSS or XPath expression that contains a given text.
     */
    public static Target containingText(String cssOrXPathLocator, String text) {
        return Target.the("Element containing text '" + text + "'")
                        .locatedBy(containingTextAndMatchingCSS(cssOrXPathLocator, text));
    }


    static Function<PageObject, List<WebElementFacade>> containingTextAndMatchingCSS(String cssOrXPathLocator, String expectedText) {
        return page -> page.withTimeoutOf(Duration.ZERO)
                .findAll(cssOrXPathLocator)
                .stream()
                .filter(element -> element.getTextContent().contains(expectedText))
                .collect(Collectors.toList());
    }
}