package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.ByAction;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.By;

public class SelectByIndexFromBy extends ByAction {
    private final Integer index;

    public SelectByIndexFromBy(Integer index, By... locators) {
        super(locators);
        this.index = index;
    }

    @Step("{0} selects index #index")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWeb.as(theUser).find(locators).selectByIndex(index);
    }
}
