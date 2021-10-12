package net.serenitybdd.screenplay.ui;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class LocatorStrategies {

    public static Function<PageObject, List<WebElementFacade>> fieldWithLabel(String labelText) {
        return onThePage -> {
            // Find the label
            WebElementFacade label = onThePage.withTimeoutOf(Duration.ZERO)
                    .find("//label[normalize-space(.)='" + AttributeValue.withEscapedQuotes(labelText) + "']");
            // Find the ID specified in the ID
            String fieldId = label.getAttribute("for");
            // Find the field with that ID
            WebElementFacade field = onThePage.withTimeoutOf(Duration.ZERO).find(By.id(fieldId));
            return Collections.singletonList(field);
        };
    }

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

}
