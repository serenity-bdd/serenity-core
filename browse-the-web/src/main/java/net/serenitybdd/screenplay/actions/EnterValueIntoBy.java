package net.serenitybdd.screenplay.actions;

import com.google.common.collect.ImmutableList;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class EnterValueIntoBy extends EnterValue {

    private final List<By> locators;

    protected WebElement resolveFor(Actor theUser) {
        return WebElementLocator.forLocators(locators).andActor(theUser);
    }

    public EnterValueIntoBy(String theText, By... locators) {
        super(theText);
        this.locators = ImmutableList.copyOf(locators);
    }

    @Step("{0} enters '#theText' into #element#followedBy")
    public <T extends Actor> void performAs(T theUser) {
        resolveFor(theUser).sendKeys(theText);
        resolveFor(theUser).sendKeys(getFollowedByKeys());
    }
}
