package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.selectactions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.ByAction;
import serenitymodel.net.thucydides.core.annotations.Step;
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
