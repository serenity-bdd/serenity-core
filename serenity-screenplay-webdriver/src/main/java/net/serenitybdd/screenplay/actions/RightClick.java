package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class RightClick {

    public static Interaction on(String cssOrXpathForElement) {
        return instrumented(RightClickOnTarget.class, Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public static Interaction on(By byLocator) {
        return instrumented(RightClickOnTarget.class, Target.the(byLocator.toString()).located(byLocator));
    }

    public static Interaction on(Target target) {
        return instrumented(RightClickOnTarget.class,target);
    }

    public static Interaction on(WebElementFacade element) {
        return instrumented(RightClickOnElement.class, element);
    }
}
