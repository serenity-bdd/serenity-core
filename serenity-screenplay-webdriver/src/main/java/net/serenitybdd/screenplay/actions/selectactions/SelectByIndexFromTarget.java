package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;
import net.thucydides.core.annotations.Step;

public class SelectByIndexFromTarget implements Interaction {
    private final Target target;
    private final Integer index;

    public SelectByIndexFromTarget(Target target, Integer index) {
        this.target = target;
        this.index = index;
    }

    @Step("{0} clicks on #target")
    public <T extends Actor> void performAs(T theUser) {
        target.resolveFor(theUser).selectByIndex(index);
    }


}
