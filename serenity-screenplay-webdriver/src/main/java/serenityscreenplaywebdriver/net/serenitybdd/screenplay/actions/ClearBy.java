package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenitymodel.net.thucydides.core.annotations.Step;
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
