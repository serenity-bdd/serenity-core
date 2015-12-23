package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.targets.Target;
import net.serenitybdd.screenplay.Action;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Step;

public class SelectByVisibleTextFromTarget implements Action {
    private final Target target;
    private final String visibleText;

    public SelectByVisibleTextFromTarget(Target target, String visibleText) {
        this.target = target;
        this.visibleText = visibleText;
    }

    @Step("{0} clicks on #target")
    public <T extends Actor> void performAs(T theUser) {
        WebElementFacade targetDropdown = BrowseTheWeb.as(theUser).moveTo(target.getCssOrXPathSelector());
        targetDropdown.selectByVisibleText(visibleText);
    }


}
