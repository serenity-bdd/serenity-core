package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.annotations.Step;
import org.openqa.selenium.Keys;

import java.util.Arrays;

public class HitElement implements Interaction {

    private Keys[] keys;
    private String pluraliser;
    private WebElementFacade element;

    public HitElement(Keys[] keys, WebElementFacade element) {
        this.keys = Arrays.copyOf(keys,keys.length);
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
