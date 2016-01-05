package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.targets.Target;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Enter {

    private final String theText;

    public Enter(String theText) {
        this.theText = theText;
    }

    public static Enter theValue(String text) {
        return new Enter(text);
    }

    public EnterValue into(String cssOrXpathForElement) {
        return instrumented(EnterValueIntoTarget.class, theText, Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public EnterValue into(Target target) {
        return instrumented(EnterValueIntoTarget.class, theText, target);
    }

    public EnterValue into(WebElementFacade element) {
        return instrumented(EnterValueIntoElement.class, theText, element);
    }

}
