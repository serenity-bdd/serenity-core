package net.serenitybdd.screenplay.actions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;

public class ClearTarget implements Interaction {
    private final Target target;

    @Step("{0} clears field #target")
    public <T extends Actor> void performAs(T theUser) {
        target.resolveFor(theUser).clear();
    }

    public ClearTarget(Target target) {
        this.target = target;
    }

}
