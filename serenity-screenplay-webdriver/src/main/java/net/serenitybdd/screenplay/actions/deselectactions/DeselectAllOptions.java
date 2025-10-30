package net.serenitybdd.screenplay.actions.deselectactions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actions.ByAction;
import net.serenitybdd.screenplay.targets.Target;

public class DeselectAllOptions extends ByAction {

    private final Target target;

    public DeselectAllOptions(Target target) {
        this.target = target;
    }

    public static DeselectAllOptions from(Target target) {
        return Instrumented.instanceOf(DeselectAllOptions.class).withProperties(target);
    }

    @Step("{0} deselects all options in #target")
    public <T extends Actor> void performAs(T theUser) {
        target.resolveFor(theUser).deselectAll();
    }

}
