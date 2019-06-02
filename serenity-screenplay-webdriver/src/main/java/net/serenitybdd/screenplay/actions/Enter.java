package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.Arrays;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Enter {

    private final CharSequence[] theText;

    public Enter(CharSequence... theText) {
        this.theText = theText;
    }

    public static Enter theValue(CharSequence... text) {
        return new Enter(text);
    }

    public static SendKeys keyValues(CharSequence... text) {
        return new SendKeys(text);
    }

    public EnterValue into(String cssOrXpathForElement) {
        return instrumented(EnterValueIntoTarget.class, Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement), theText);
    }

    public EnterValue into(Target target) {
        return instrumented(EnterValueIntoTarget.class, target, theText);
    }

    public EnterValue into(WebElementFacade element) {
        return instrumented(EnterValueIntoElement.class, element, theText);
    }

    public EnterValue into(By... locators) {
        return instrumented(EnterValueIntoBy.class, Arrays.asList(locators), theText);
    }
}
