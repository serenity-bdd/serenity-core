package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenitycore.net.serenitybdd.core.pages.WebElementFacade;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.ClickInteraction;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;

import static serenityscreenplay.net.serenitybdd.screenplay.Tasks.instrumented;

public class UncheckCheckbox {
    public static ClickInteraction of(Target target) { return instrumented(CheckCheckboxOfTarget.class, target, false); }

    public static ClickInteraction of(String cssOrXpathForElement) {
        return instrumented(
                CheckCheckboxOfTarget.class,
                Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement),
                false
        );
    }

    public static ClickInteraction of(WebElementFacade element) {
        return instrumented(CheckCheckboxOfElement.class, element, false);
    }
}
