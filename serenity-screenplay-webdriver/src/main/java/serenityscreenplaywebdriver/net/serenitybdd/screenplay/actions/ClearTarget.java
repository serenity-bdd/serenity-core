package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;
import serenitymodel.net.thucydides.core.annotations.Step;

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
