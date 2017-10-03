package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import net.thucydides.core.annotations.Step;

public class EnterValueIntoTarget extends EnterValue {

    private Target target;

    public EnterValueIntoTarget(String theText, Target target) {
        super(theText);
        this.target = target;
    }

    @Step("{0} enters '#theText' into #target")
    public <T extends Actor> void performAs(T theUser) {
        target.resolveFor(theUser).type(theText);
        if (getFollowedByKeys().length > 0) {
            target.resolveFor(theUser).sendKeys(getFollowedByKeys());
        }
    }
}
