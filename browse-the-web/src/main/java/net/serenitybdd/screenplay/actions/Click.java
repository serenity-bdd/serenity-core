package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.targets.Target;
import net.serenitybdd.screenplay.Performable;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Click {

    public static Performable on(String cssOrXpathForElement) {
        return instrumented(ClickOnTarget.class, Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public static Performable on(Target target) {
        return instrumented(ClickOnTarget.class,target);
    }

    public static Performable on(WebElementFacade element) {
        return instrumented(ClickOnElement.class, element);
    }

}
