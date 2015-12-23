package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.targets.Target;
import net.serenitybdd.screenplay.Action;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Step;

public class SelectByValueFromTarget implements Action {
    private final Target target;
    private final String value;

    public SelectByValueFromTarget(Target target, String value) {
        this.target = target;
        this.value = value;
    }

    @Step("{0} clicks on #target")
    public <T extends Actor> void performAs(T theUser) {
        WebElementFacade targetDropdown = BrowseTheWeb.as(theUser).moveTo(target.getCssOrXPathSelector());
        targetDropdown.selectByVisibleText(value);
    }


}
