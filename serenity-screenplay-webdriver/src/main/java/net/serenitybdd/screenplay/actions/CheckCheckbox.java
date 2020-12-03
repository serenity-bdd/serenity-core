package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.ClickInteraction;
import net.serenitybdd.screenplay.targets.Target;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class CheckCheckbox {
    public static ClickInteraction of(Target target) { return instrumented(CheckCheckboxOfTarget.class, target, true); }

    public static ClickInteraction of(String cssOrXpathForElement) {
        return instrumented(
                CheckCheckboxOfTarget.class,
                Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement),
                true
        );
    }

    public static ClickInteraction of(WebElementFacade element) {
        return instrumented(CheckCheckboxOfElement.class, element, true);
    }



}
