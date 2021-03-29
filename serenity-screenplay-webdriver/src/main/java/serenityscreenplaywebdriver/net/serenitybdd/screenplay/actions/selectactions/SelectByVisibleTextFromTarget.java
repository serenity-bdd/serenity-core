package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.selectactions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;
import serenitymodel.net.thucydides.core.annotations.Step;

public class SelectByVisibleTextFromTarget implements Interaction {
    private final Target target;
    private final String visibleText;

    public SelectByVisibleTextFromTarget(Target target, String visibleText) {
        this.target = target;
        this.visibleText = visibleText;
    }

    @Step("{0} select #visibleText on #target")
    public <T extends Actor> void performAs(T theUser) {
        target.resolveFor(theUser).selectByVisibleText(visibleText);
    }


}
