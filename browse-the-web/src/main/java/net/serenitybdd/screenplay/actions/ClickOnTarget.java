package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.targets.Target;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Step;

public class ClickOnTarget extends WebAction {
    private final Target target;

    @Step("{0} clicks on #target")
    public <T extends Actor> void performAs(T theUser) {
        findBy(target).then().click();
    }

    public ClickOnTarget(Target target) {
        this.target = target;
    }

}
