package net.serenitybdd.screenplay.actions.deselectactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;
import net.thucydides.core.annotations.Step;

public class DeselectByVisibleTextFromTarget implements Interaction {
    private final Target target;
    private final String visibleText;

    public DeselectByVisibleTextFromTarget(Target target, String visibleText) {
        this.target = target;
        this.visibleText = visibleText;
    }

    @Step("{0} deselects #visibleText in #target")
    public <T extends Actor> void performAs(T theUser) {
        target.resolveFor(theUser).deselectByVisibleText(visibleText);
    }

}
