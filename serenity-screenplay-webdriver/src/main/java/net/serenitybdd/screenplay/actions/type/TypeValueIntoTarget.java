package net.serenitybdd.screenplay.actions.type;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import net.thucydides.core.annotations.Step;

public class TypeValueIntoTarget extends TypeValue {

    private Target target;

    public TypeValueIntoTarget(String theText, Target target) {
        super(theText);
        this.target = target;
    }

    @Step("{0} enters '#theText' into #target")
    public <T extends Actor> void performAs(T theUser) {
        target.resolveFor(theUser).sendKeys(theText);
        target.resolveFor(theUser).sendKeys(getFollowedByKeys());

    }
}
