package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.targets.Target;

public class Enter {

    private final String theText;

    public Enter(String theText) {
        this.theText = theText;
    }

    public static Enter theValue(String text) {
        return new Enter(text);
    }

    public EnterValue into(String cssOrXpathForElement) {
        return new EnterValueIntoTarget(theText, Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public EnterValue into(Target target) {
        return new EnterValueIntoTarget(theText, target);
    }

    public EnterValue into(WebElementFacade element) {
        return new EnterValueIntoElement(theText, element);
    }

}
