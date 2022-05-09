package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * If this element is a form entry element, this will reset its value.
 */
public class Clear {

    public static Interaction field(String cssOrXpathForElement) {
        return instrumented(ClearTarget.class, Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public static Interaction field(Target target) {
        return instrumented(ClearTarget.class,target);
    }

    public static Interaction field(WebElementFacade element) {
        return instrumented(ClearElement.class, element);
    }

    public static Interaction field(By... locators) {
        return instrumented(ClearBy.class, NewList.of(locators));
    }

}
