package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;
import serenitymodel.net.thucydides.core.annotations.Step;

public class EnterValueIntoTarget extends EnterValue {

    private Target target;

    public EnterValueIntoTarget(Target target, CharSequence... theText) {
        super(theText);
        this.target = target;
    }

    @Step("{0} enters #theTextAsAString into #target")
    public <T extends Actor> void performAs(T theUser) {
        target.resolveFor(theUser).type(theText);
        if (getFollowedByKeys().length > 0) {
            target.resolveFor(theUser).sendKeys(getFollowedByKeys());
        }
    }
}
