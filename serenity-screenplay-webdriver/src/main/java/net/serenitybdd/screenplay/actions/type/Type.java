package net.serenitybdd.screenplay.actions.type;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.Arrays;

public class Type {

    private final CharSequence[] theText;

    public Type(CharSequence... theText) {
        this.theText = theText;
    }

    public static Type theValue(CharSequence... text) {
        return new Type(text);
    }

    public TypeValue into(String cssOrXpathForElement) {
        return new TypeValueIntoTarget(Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement), theText);
    }

    public TypeValue into(Target target) {
        return new TypeValueIntoTarget(target, theText);
    }

    public TypeValue into(WebElementFacade element) {
        return new TypeValueIntoElement(element, theText);
    }

    public TypeValue into(By... locators) {
        return new TypeValueIntoBy(Arrays.asList(locators), theText);
    }
}
