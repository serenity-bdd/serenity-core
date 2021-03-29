package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.selectactions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;
import serenitymodel.net.thucydides.core.annotations.Step;

public class SelectByIndexFromTarget implements Interaction {
    private final Target target;
    private final Integer index;

    public SelectByIndexFromTarget(Target target, Integer index) {
        this.target = target;
        this.index = index;
    }

    @Step("{0} selects index #index in #target")
    public <T extends Actor> void performAs(T theUser) {
        target.resolveFor(theUser).selectByIndex(index);
    }


}
