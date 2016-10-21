package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.ByAction;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.By;

public class SelectByVisibleTextFromBy extends ByAction {
    private final String visibleText;

    public SelectByVisibleTextFromBy(String visibleText, By... locators) {
        super(locators);
        this.visibleText = visibleText;
    }

    @Step("{0} selects #visibleText")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWeb.as(theUser).find(locators).selectByVisibleText(visibleText);
    }
}
