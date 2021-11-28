package net.serenitybdd.screenplay.ui;

import net.serenitybdd.core.pages.ListOfWebElementFacades;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class LocatorStrategies {

    /**
     * Locate a form element with a corresponding label
     */
    public static Function<PageObject, List<WebElementFacade>> fieldWithLabel(String labelText) {
        return onThePage -> {
            // Find the label
            ListOfWebElementFacades matchingLabelsWithForAttribute = onThePage.findAll(labelsWithText(labelText));
            if (matchingLabelsWithForAttribute.isEmpty()) {
                return new ArrayList<>();
            }
            // Let's see if the 'for' attribute has been specified
            String fieldId = matchingLabelsWithForAttribute.get(0).getAttribute("for");
            // If there is no for attribute, click on the label itself
            if ((fieldId == null) || fieldId.isEmpty()) {
                // No? No worries, let's just click on the label instead
                return matchingLabelsWithForAttribute;
            } else {
                // If there is a for attribute, Find the field with that ID
                return onThePage.findAll(By.id(fieldId));
            }
        };
    }

    /**
     * Locate elements nested inside another element
     */
    public static Function<PageObject, List<WebElementFacade>> findNestedElements(Target container, Target nestedElement) {
        return page -> {
            // Find the container
            WebElementFacade containerElement = container.resolveFor(page);
            // Find the matching child fields
            return containerElement
                    .withTimeoutOf(Duration.ZERO)
                    .thenFindAll(
                            nestedElement.selectors(page.getDriver()).toArray(new By[]{})
                    );
        };
    }

    private static String labelsWithText(String labelText) {
        return "//label[normalize-space(.)='" + CSSAttributeValue.withEscapedQuotes(labelText) + "']";
    }
}
