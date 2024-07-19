package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.annotations.Step;

public class SendKeysIntoTarget extends EnterValue {

    private Target target;

    public SendKeysIntoTarget(Target target, CharSequence... theText) {
        super(theText);
        this.target = target;
    }

    @Step("{0} enters #theTextAsAString into #target")
    public <T extends Actor> void performAs(T theUser) {
        textValue().ifPresent(
                text -> target.resolveFor(theUser).sendKeys(theText)
        );
        if (getFollowedByKeys().length > 0) {
            target.resolveFor(theUser).sendKeys(getFollowedByKeys());
        }
    }
}
