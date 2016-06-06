package net.serenitybdd.screenplay.actions.type;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Type {

    private final String theText;

    public Type(String theText) {
        this.theText = theText;
    }

    public static Type theValue(String text) {
        return new Type(text);
    }

    public TypeValue into(String cssOrXpathForElement) {
        return instrumented(TypeValueIntoTarget.class, theText, Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public TypeValue into(Target target) {
        return instrumented(TypeValueIntoTarget.class, theText, target);
    }

    public TypeValue into(WebElementFacade element) {
        return instrumented(TypeValueIntoElement.class, theText, element);
    }

    public TypeValue into(By... locators) {
        return instrumented(TypeValueIntoBy.class, theText, locators);
    }
}
