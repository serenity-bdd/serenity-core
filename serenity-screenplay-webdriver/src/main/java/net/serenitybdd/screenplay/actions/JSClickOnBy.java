package net.serenitybdd.screenplay.actions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;

import java.util.List;

public class JSClickOnBy extends ByAction {

    @Step("{0} clicks on #locators")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWeb.as(theUser).evaluateJavascript("arguments[0].click();", resolveFor(theUser));
    }

    public JSClickOnBy(By... locators) {
        super(locators);
    }

    public JSClickOnBy(List<By> locators) {
        super(locators.toArray(new By[]{}));
    }

}
