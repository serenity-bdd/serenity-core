package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.core.targets.Target;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actions.WebAction;
import net.thucydides.core.annotations.Step;

public class SelectByVisibleTextFromTarget  extends WebAction {
    private final Target target;
    private final String visibleText;

    public SelectByVisibleTextFromTarget(Target target, String visibleText) {
        this.target = target;
        this.visibleText = visibleText;
    }

    @Step("{0} clicks on #target")
    public <T extends Actor> void performAs(T theUser) {
        moveTo(target.getCssOrXPathSelector()).selectByVisibleText(visibleText);
    }


}
