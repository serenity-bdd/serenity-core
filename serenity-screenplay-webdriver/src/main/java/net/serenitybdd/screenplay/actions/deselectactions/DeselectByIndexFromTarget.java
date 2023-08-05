package net.serenitybdd.screenplay.actions.deselectactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.annotations.Step;

public class DeselectByIndexFromTarget implements Interaction {
    private Target target;
    private Integer index;

    public DeselectByIndexFromTarget() {}

    public DeselectByIndexFromTarget(Target target, Integer index) {
        this.target = target;
        this.index = index;
    }

    @Step("{0} deselects index #index in #target")
    public <T extends Actor> void performAs(T theUser) {
        target.resolveFor(theUser).deselectByIndex(index);
    }


}
