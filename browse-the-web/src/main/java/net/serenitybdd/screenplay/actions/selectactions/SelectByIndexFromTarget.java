package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.core.targets.Target;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actions.WebAction;
import net.thucydides.core.annotations.Step;

public class SelectByIndexFromTarget extends WebAction {
    private final Target target;
    private final Integer index;

    public SelectByIndexFromTarget(Target target, Integer index) {
        this.target = target;
        this.index = index;
    }

    @Step("{0} clicks on #target")
    public <T extends Actor> void performAs(T theUser) {
        moveTo(target.getCssOrXPathSelector()).and().selectByIndex(index);
    }


}
