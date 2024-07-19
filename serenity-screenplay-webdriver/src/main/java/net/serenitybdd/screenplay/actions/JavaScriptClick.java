package net.serenitybdd.screenplay.actions;

import net.serenitybdd.model.collect.NewList;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class JavaScriptClick {

    public static Interaction on(String cssOrXpathForElement) {
        return instrumented(JSClickOnTarget.class, Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public static Interaction on(Target target) {
        return instrumented(JSClickOnTarget.class,target);
    }

    public static Interaction on(WebElementFacade element) {
        return instrumented(JSClickOnElement.class, element);
    }

    public static Interaction on(By... locators) {
        return instrumented(JSClickOnBy.class, NewList.of(locators));
    }

}
