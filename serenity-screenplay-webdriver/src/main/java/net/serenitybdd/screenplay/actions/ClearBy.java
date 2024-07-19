package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.annotations.Step;
import org.openqa.selenium.By;

import java.util.List;

public class ClearBy extends ByAction {

    @Step("{0} clears field #locators")
    public <T extends Actor> void performAs(T theUser) {
        resolveFor(theUser).clear();
    }

    public ClearBy(By... locators) {
        super(locators);
    }

    public ClearBy(List<By> locators) {
        super(locators.toArray(new By[]{}));
    }

}
