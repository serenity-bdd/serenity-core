package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.Arrays;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class SendKeys {

    private final CharSequence[] theText;

    public SendKeys(CharSequence... theText) {
        this.theText = theText;
    }

    public static SendKeys of(CharSequence... text) {
        return new SendKeys(text);
    }

    public EnterValue into(String cssOrXpathForElement) {
        return instrumented(SendKeysIntoTarget.class, Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement), theText);
    }

    public EnterValue into(Target target) {
        return instrumented(SendKeysIntoTarget.class, target, theText);
    }

    public EnterValue into(WebElementFacade element) {
        return instrumented(SendKeystoElement.class, element, theText);
    }

    public EnterValue into(By... locators) {
        return instrumented(SendKeysIntoBy.class, Arrays.asList(locators), theText);
    }
}
