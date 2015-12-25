package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.core.targets.Target;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actions.WebAction;
import net.thucydides.core.annotations.Step;

public class SelectByValueFromTarget extends WebAction {
    private final Target target;
    private final String value;

    public SelectByValueFromTarget(Target target, String value) {
        this.target = target;
        this.value = value;
    }

    @Step("{0} clicks on #target")
    public <T extends Actor> void performAs(T theUser) {
        moveTo(target.getCssOrXPathSelector()).and().selectByVisibleText(value);
    }


}
