package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.targets.Target;
import net.serenitybdd.screenplay.Action;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Step;

public class EnterValueIntoTarget implements Action {

    private String theText;
    private Target target;

    public EnterValueIntoTarget(String theText, Target target) {
        this.theText = theText;
        this.target = target;
    }

    @Step("{0} enters '#theText' into #target")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWeb.as(theUser).moveTo(target).then().type(theText);
    }
}
