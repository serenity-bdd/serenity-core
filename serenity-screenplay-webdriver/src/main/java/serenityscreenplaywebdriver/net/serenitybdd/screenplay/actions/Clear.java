package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenitymodel.net.serenitybdd.core.collect.NewList;
import serenitycore.net.serenitybdd.core.pages.WebElementFacade;
import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static serenityscreenplay.net.serenitybdd.screenplay.Tasks.instrumented;

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
