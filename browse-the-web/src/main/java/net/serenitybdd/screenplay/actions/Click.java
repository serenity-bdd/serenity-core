package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Action;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Click {

    public static Action on(String cssOrXpathForElement) {
        return instrumented(ClickOnTarget.class, Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public static Action on(Target target) {
        return instrumented(ClickOnTarget.class,target);
    }

    public static Action on(WebElementFacade element) {
        return instrumented(ClickOnElement.class, element);
    }

    public static Action on(By... locators) {
        return instrumented(ClickOnBy.class, locators);
    }

}
