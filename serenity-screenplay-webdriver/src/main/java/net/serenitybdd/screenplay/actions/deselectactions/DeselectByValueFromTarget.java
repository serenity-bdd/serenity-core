package net.serenitybdd.screenplay.actions.deselectactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;
import net.thucydides.core.annotations.Step;

public class DeselectByValueFromTarget implements Interaction {
    private final Target target;
    private final String value;

    public DeselectByValueFromTarget(Target target, String value) {
        this.target = target;
        this.value = value;
    }

    @Step("{0} deselects #value in #target")
    public <T extends Actor> void performAs(T theUser) {
        target.resolveFor(theUser).deselectByVisibleText(value);
    }

}
