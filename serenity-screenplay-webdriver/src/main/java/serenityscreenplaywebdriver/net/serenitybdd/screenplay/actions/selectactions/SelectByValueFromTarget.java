package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.selectactions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;
import serenitymodel.net.thucydides.core.annotations.Step;

public class SelectByValueFromTarget implements Interaction {
    private final Target target;
    private final String value;

    public SelectByValueFromTarget(Target target, String value) {
        this.target = target;
        this.value = value;
    }

    @Step("{0} selects #value in #target")
    public <T extends Actor> void performAs(T theUser) {
        target.resolveFor(theUser).selectByValue(value);
    }


}
