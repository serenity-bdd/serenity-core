package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;
import serenitymodel.net.thucydides.core.annotations.Step;

public class JSClickOnTarget implements Interaction {
    private final Target target;

    @Step("{0} clicks on #target")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWeb.as(theUser).evaluateJavascript("arguments[0].click();", target.resolveFor(theUser));
    }

    public JSClickOnTarget(Target target) {
        this.target = target;
    }

}
