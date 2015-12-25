package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.targets.Target;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Step;

public class EnterValueIntoTarget extends WebAction {

    private String theText;
    private Target target;

    public EnterValueIntoTarget(String theText, Target target) {
        this.theText = theText;
        this.target = target;
    }

    @Step("{0} enters '#theText' into #target")
    public <T extends Actor> void performAs(T theUser) {
        ensurePresenceOf(target);
        moveTo(target).then().type(theText);
    }
}
