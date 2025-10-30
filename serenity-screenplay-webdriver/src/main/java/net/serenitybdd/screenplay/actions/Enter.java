package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.Arrays;

import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Enter a value into a field, first waiting until the field is enabled, and then clearing the field of any current values,
 * before entering the specified value.
 * To perform the equivalent of Selenium `sendKeys()`, you can use `Enter.keyValue()` instead of `Enter.theValue()`
 */
public class Enter {

    private final CharSequence[] theText;

    public Enter(CharSequence... theText) {
        if (containsOnlyNullValues(theText)) {
            this.theText = new CharSequence[]{""};
        } else {
            this.theText = theText;
        }
    }

    private boolean containsOnlyNullValues(CharSequence[] theText) {
        return theText.length == 1 && theText[0] == null;
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
