package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.deselectactions;

import serenitycore.net.serenitybdd.core.steps.Instrumented;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.ByAction;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;
import serenitymodel.net.thucydides.core.annotations.Step;

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
