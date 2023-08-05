package net.serenitybdd.screenplay.actions.type;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.annotations.Step;

public class TypeValueIntoTarget extends TypeValue {

    private Target target;

    public TypeValueIntoTarget() {}

    public TypeValueIntoTarget(Target target, CharSequence... theText) {
        super(theText);
        this.target = target;
    }

    @Step("{0} enters #theTextAsAString into #target")
    public <T extends Actor> void performAs(T theUser) {
        textValue().ifPresent(
            text -> target.resolveFor(theUser).sendKeys(text)
        );
        if(getFollowedByKeys().length!=0) {        
            target.resolveFor(theUser).sendKeys(getFollowedByKeys());
        }
    }
}
