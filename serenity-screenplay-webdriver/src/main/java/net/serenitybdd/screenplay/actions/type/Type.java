package net.serenitybdd.screenplay.actions.type;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.Arrays;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Type {

    private final CharSequence[] theText;

    public Type(CharSequence... theText) {
        this.theText = theText;
    }

    public static Type theValue(CharSequence... text) {
        return new Type(text);
    }

    public TypeValue into(String cssOrXpathForElement) {
        return instrumented(TypeValueIntoTarget.class, Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement), theText);
    }

    public TypeValue into(Target target) {
        return instrumented(TypeValueIntoTarget.class, target, theText);
    }

    public TypeValue into(WebElementFacade element) {
        return instrumented(TypeValueIntoElement.class, element, theText);
    }

    public TypeValue into(By... locators) {
        return instrumented(TypeValueIntoBy.class, Arrays.asList(locators), theText);
    }
}
