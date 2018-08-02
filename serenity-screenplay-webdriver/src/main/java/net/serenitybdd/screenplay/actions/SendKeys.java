package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class SendKeys {

    private final String theText;

    public SendKeys(String theText) {
        this.theText = theText;
    }

    public static SendKeys of(String text) {
        return new SendKeys(text);
    }

    public EnterValue into(String cssOrXpathForElement) {
        return instrumented(SendKeysIntoTarget.class, theText, Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public EnterValue into(Target target) {
        return instrumented(SendKeysIntoTarget.class, theText, target);
    }

    public EnterValue into(WebElementFacade element) {
        return instrumented(SendKeystoElement.class, theText, element);
    }

    public EnterValue into(By... locators) {
        return instrumented(SendKeysIntoBy.class, theText, locators);
    }
}
