package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.targets.Target;
import net.serenitybdd.screenplay.Action;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Step;

public class SelectByIndexFromTarget implements Action {
    private final Target target;
    private final Integer index;

    public SelectByIndexFromTarget(Target target, Integer index) {
        this.target = target;
        this.index = index;
    }

    @Step("{0} clicks on #target")
    public <T extends Actor> void performAs(T theUser) {
        WebElementFacade targetDropdown = BrowseTheWeb.as(theUser).moveTo(target.getCssOrXPathSelector());
        targetDropdown.selectByIndex(index);
    }


}
