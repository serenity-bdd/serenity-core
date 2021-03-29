package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenitymodel.net.serenitybdd.core.collect.NewList;
import serenitycore.net.serenitybdd.core.pages.WebElementFacade;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.ClickInteraction;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static serenityscreenplay.net.serenitybdd.screenplay.Tasks.instrumented;

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
