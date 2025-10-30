package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.model.collect.NewList;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Click on a button or element.
 */
public class Click {

    public static ClickInteraction on(String cssOrXpathForElement) {
        return instrumented(ClickOnTarget.class, Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public static ClickInteraction on(Target target) {
        return instrumented(ClickOnTarget.class,target);
    }

    public static ClickInteraction on(WebElementFacade element) {
        return instrumented(ClickOnElement.class, element);
    }

    public static ClickInteraction on(By... locators) {
        return instrumented(ClickOnBy.class, NewList.of(locators));
    }
}
