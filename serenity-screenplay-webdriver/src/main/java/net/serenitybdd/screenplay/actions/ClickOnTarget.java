package net.serenitybdd.screenplay.actions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;

public class ClickOnTarget extends ClickOnClickable {
    private final Target target;

    @Override
    @Step("{0} clicks on #target")
    public <T extends Actor> void performAs(T theUser) {
        target.resolveFor(theUser).click(getClickStrategy());
    }

    public ClickOnTarget(Target target) {
        this.target = target;
    }

}
