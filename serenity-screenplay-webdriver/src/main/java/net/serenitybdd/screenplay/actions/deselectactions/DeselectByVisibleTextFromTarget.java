package net.serenitybdd.screenplay.actions.deselectactions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;

public class DeselectByVisibleTextFromTarget implements Interaction {
    private Target target;
    private String visibleText;

    public DeselectByVisibleTextFromTarget() {}

    public DeselectByVisibleTextFromTarget(Target target, String visibleText) {
        this.target = target;
        this.visibleText = visibleText;
    }

    @Step("{0} deselects #visibleText in #target")
    public <T extends Actor> void performAs(T theUser) {
        target.resolveFor(theUser).deselectByVisibleText(visibleText);
    }

}
