package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.type;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;
import serenitymodel.net.thucydides.core.annotations.Step;

public class TypeValueIntoTarget extends TypeValue {

    private Target target;

    public TypeValueIntoTarget(Target target, CharSequence... theText) {
        super(theText);
        this.target = target;
    }

    @Step("{0} enters #theTextAsAString into #target")
    public <T extends Actor> void performAs(T theUser) {
        target.resolveFor(theUser).sendKeys(theText);
        if(getFollowedByKeys().length!=0) {        
            target.resolveFor(theUser).sendKeys(getFollowedByKeys());
        }
    }
}
