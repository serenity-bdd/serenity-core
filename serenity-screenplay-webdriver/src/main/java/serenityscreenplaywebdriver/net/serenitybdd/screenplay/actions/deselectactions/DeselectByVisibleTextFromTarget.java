package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.deselectactions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;
import serenitymodel.net.thucydides.core.annotations.Step;

public class DeselectByVisibleTextFromTarget implements Interaction {
    private final Target target;
    private final String visibleText;

    public DeselectByVisibleTextFromTarget(Target target, String visibleText) {
        this.target = target;
        this.visibleText = visibleText;
    }

    @Step("{0} deselects #visibleText in #target")
    public <T extends Actor> void performAs(T theUser) {
        target.resolveFor(theUser).deselectByVisibleText(visibleText);
    }

}
