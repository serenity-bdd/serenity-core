package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.targets.Target;
import net.serenitybdd.screenplay.Action;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Step;

public class ClickOnTarget implements Action {
    private final Target target;

    @Step("{0} clicks on #target")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWeb.as(theUser).findBy(target).then().click();
    }

    public ClickOnTarget(Target target) {
        this.target = target;
    }

}
