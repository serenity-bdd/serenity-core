package net.serenitybdd.screenplay.ui;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementFacadeImpl;
import net.serenitybdd.core.selectors.Selectors;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LocatorStrategies {

    public static Function<SearchContext, List<WebElementFacade>> fieldWithLabel(String labelText) {
        return searchContext -> {
            // Find the label
            List<WebElement> matchingLabelsWithForAttribute = searchContext.findElements(By.xpath(labelsWithText(labelText)));
            if (matchingLabelsWithForAttribute.isEmpty()) {
                return new ArrayList<>();
            }
            // Let's see if the 'for' attribute has been specified
            String fieldId = matchingLabelsWithForAttribute.get(0).getAttribute("for");
            // If there is no for attribute, click on the label itself
            if ((fieldId == null) || fieldId.isEmpty()) {
                // No? No worries, let's just click on the label instead
                return WebElementFacadeImpl.fromWebElements(matchingLabelsWithForAttribute);
            } else {
                // If there is a for attribute, Find the field with that ID
                return WebElementFacadeImpl.fromWebElements(searchContext.findElements(By.id(fieldId)));
            }
        };
    }

    public static Function<SearchContext, List<WebElementFacade>> findNestedElements(Target container, Target nestedElement) {
        return searchContext -> {
            // Find the first matching container
            return container.resolveAllFor(searchContext)
                    .stream()
                    .flatMap(
                            webElementFacade -> webElementFacade.withTimeoutOf(Duration.ZERO).findNestedElementsMatching(nestedElement).stream()
                    ).collect(Collectors.toList());
        };
    }

    public static Function<SearchContext, List<WebElementFacade>> containingTextAndMatchingCSS(String cssOrXPathLocator, String expectedText) {
        return searchContext ->
                WebElementFacadeImpl.fromWebElements(
                        searchContext.findElements(Selectors.xpathOrCssSelector(cssOrXPathLocator))
                                .stream()
                                .filter(WebElement::isDisplayed)
                                .filter(element -> element.getAttribute("textContent").contains(expectedText))
                                .collect(Collectors.toList()));
    }


    public static Function<SearchContext, List<WebElementFacade>> containingTextAndMatchingCSS(List<String> cssOrXPathLocators, String expectedText) {
        return searchContext -> {
            List<WebElement> matchingElements = new ArrayList<>();
            for (String cssOrXPathLocator : cssOrXPathLocators) {
                matchingElements.addAll(
                        WebElementFacadeImpl.fromWebElements(
                                searchContext.findElements(Selectors.xpathOrCssSelector(cssOrXPathLocator))
                                        .stream()
                                        .filter(WebElement::isDisplayed)
                                        .filter(element -> element.getAttribute("textContent").contains(expectedText))
                                        .collect(Collectors.toList()))
                );
            }
            return WebElementFacadeImpl.fromWebElements(matchingElements);
        };
    }

    public static Function<SearchContext, List<WebElementFacade>> containingTextAndMatchingCSSIgnoringCase(String cssOrXPathLocator, String expectedText) {
        return searchContext ->
                WebElementFacadeImpl.fromWebElements(
                        searchContext.findElements(Selectors.xpathOrCssSelector(cssOrXPathLocator))
                                .stream()
                                .filter(WebElement::isDisplayed)
                                .filter(element -> element.getAttribute("textContent").toLowerCase().contains(expectedText.toLowerCase()))
                                .collect(Collectors.toList()));
    }


    public static Function<SearchContext, List<WebElementFacade>> containingTextAndMatchingCSSIgnoringCase(List<String> cssOrXPathLocators, String expectedText) {
        return searchContext -> {
            List<WebElement> matchingElements = new ArrayList<>();
            for (String cssOrXPathLocator : cssOrXPathLocators) {
                matchingElements.addAll(
                        WebElementFacadeImpl.fromWebElements(
                                searchContext.findElements(Selectors.xpathOrCssSelector(cssOrXPathLocator))
                                        .stream()
                                        .filter(WebElement::isDisplayed)
                                        .filter(element -> element.getAttribute("textContent").toLowerCase().contains(expectedText.toLowerCase()))
                                        .collect(Collectors.toList()))
                );
            }
            return WebElementFacadeImpl.fromWebElements(matchingElements);
        };
    }

    public static Function<SearchContext, List<WebElementFacade>> containingTextAndBy(By byLocator, String expectedText) {
        return searchContext ->
                WebElementFacadeImpl.fromWebElements(
                        searchContext.findElements(byLocator)
                                .stream()
                                .filter(WebElement::isDisplayed)
                                .filter(element -> containsText(element,expectedText))
                                .collect(Collectors.toList())
                );
    }

    public static Function<SearchContext, List<WebElementFacade>> containingTextAndByIgnoringCase(By byLocator, String expectedText) {
        return searchContext ->
                WebElementFacadeImpl.fromWebElements(
                        searchContext.findElements(byLocator)
                                .stream()
                                .filter(WebElement::isDisplayed)
                                .filter(element -> containsTextIgnoringCase(element,expectedText))
                                .collect(Collectors.toList())
                );
    }

    private static boolean containsText(WebElement element, String expectedText) {
        return element.getText().contains(expectedText);
    }

    private static boolean containsTextIgnoringCase(WebElement element, String expectedText) {
        return element.getText().toLowerCase().contains(expectedText.toLowerCase());
    }

    private static String labelsWithText(String labelText) {
        return ".//label[normalize-space(.)='" + CSSAttributeValue.withEscapedQuotes(labelText) + "']";
    }

}
