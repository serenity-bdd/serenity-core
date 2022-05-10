package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Double-click on a button or element
 */
public class DoubleClick {

    public static Interaction on(String cssOrXpathForElement) {
        return instrumented(DoubleClickOnTarget.class, Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public static Interaction on(Target target) {
        return instrumented(DoubleClickOnTarget.class,target);
    }

    public static Interaction on(WebElementFacade element) {
        return instrumented(DoubleClickOnElement.class, element);
    }

    public static Interaction on(By... locators) {
        return instrumented(DoubleClickOnBy.class, NewList.of(locators));
    }

}
