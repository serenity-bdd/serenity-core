package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

public class HitBy extends ByAction {

    private Keys[] keys;
    private String pluraliser;

    public HitBy(Keys[] keys, By... locators) {
        super(locators);
        this.keys = keys;
    }

    @Step("{0} hits the '#keys' key#pluraliser")
    public <T extends Actor> void performAs(T theUser) {
        pluraliser = pluralSuffixOf(keys);
        resolveFor(theUser).sendKeys(keys);
    }

    private String pluralSuffixOf(Keys[] keys) {
        return keys.length > 1 ? "s" : "";
    }
}
