package net.serenitybdd.screenplay.actions.deselectactions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;

public class DeselectByValueFromTarget implements Interaction {
    private Target target;
    private String value;

    public DeselectByValueFromTarget() {}

    public DeselectByValueFromTarget(Target target, String value) {
        this.target = target;
        this.value = value;
    }

    @Step("{0} deselects #value in #target")
    public <T extends Actor> void performAs(T theUser) {
        target.resolveFor(theUser).deselectByVisibleText(value);
    }

}
