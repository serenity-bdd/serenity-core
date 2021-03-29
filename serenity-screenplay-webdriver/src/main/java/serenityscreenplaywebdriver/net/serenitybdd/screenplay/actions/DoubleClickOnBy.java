package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import serenitymodel.net.thucydides.core.annotations.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class DoubleClickOnBy extends ByAction {

    @Step("{0} double-clicks on #locators")
    public <T extends Actor> void performAs(T theUser) {
        WebElement element = resolveFor(theUser);
        BrowseTheWeb.as(theUser).withAction().moveToElement(element).doubleClick().perform();
    }

    public DoubleClickOnBy(By... locators) {
        super(locators);
    }

    public DoubleClickOnBy(List<By> locators) {
        super(locators.toArray(new By[]{}));
    }

}
