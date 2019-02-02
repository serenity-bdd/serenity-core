package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.ByAction;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.By;

public class SelectByValueFromBy extends ByAction {
    private final String value;

    public SelectByValueFromBy(String value, By... locators) {
        super(locators);
        this.value = value;
    }

    @Step("{0} selects #value in #locators")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWeb.as(theUser).find(locators).selectByValue(value);
    }

}
