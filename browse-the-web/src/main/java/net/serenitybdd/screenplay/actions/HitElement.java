package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Action;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.Keys;

public class HitElement implements Action {

    private Keys[] keys;
    private String pluraliser;
    private WebElementFacade element;

    public HitElement(Keys[] keys, WebElementFacade element) {
        this.keys = keys;
        this.element = element;
    }

    @Step("{0} hits the '#keys' key#pluraliser")
    public <T extends Actor> void performAs(T theUser) {
        pluraliser = pluralSuffixOf(keys);
        element.sendKeys(keys);
    }

    private String pluralSuffixOf(Keys[] keys) {
        return keys.length > 1 ? "s" : "";
    }
}
