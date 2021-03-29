package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;
import serenitymodel.net.thucydides.core.annotations.Step;

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
