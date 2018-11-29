package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.By;

import java.util.List;

public class ClickOnBy extends ByAction {

    @Step("{0} clicks on #locators")
    public <T extends Actor> void performAs(T theUser) {
        resolveFor(theUser).click();
    }

    public ClickOnBy(By... locators) {
        super(locators);
    }

    public ClickOnBy(List<By> locators) {
        super(locators.toArray(new By[]{}));
    }

}
