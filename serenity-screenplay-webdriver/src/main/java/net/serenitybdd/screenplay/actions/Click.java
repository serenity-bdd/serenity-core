package net.serenitybdd.screenplay.actions;

import com.google.common.collect.Lists;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Click {

    public static Interaction on(String cssOrXpathForElement) {
        return instrumented(ClickOnTarget.class, Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public static Interaction on(Target target) {
        return instrumented(ClickOnTarget.class,target);
    }

    public static Interaction on(WebElementFacade element) {
        return instrumented(ClickOnElement.class, element);
    }

    public static Interaction on(By... locators) {
        return instrumented(ClickOnBy.class, Lists.newArrayList(locators));
    }

}
