package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.By;

public class ClickOnBy extends ByAction {

    @Step("{0} clicks on #target")
    public <T extends Actor> void performAs(T theUser) {
        resolveFor(theUser).click();
    }

    public ClickOnBy(By... locators) {
        super(locators);
    }

}
