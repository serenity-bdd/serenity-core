package net.serenitybdd.screenplay.actions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SendKeysIntoBy extends EnterValue {

    private final List<By> locators;
    private final String locatorNames;

    protected WebElement resolveFor(Actor theUser) {
        return WebElementLocator.forLocators(locators).andActor(theUser);
    }

    public SendKeysIntoBy(List<By> locators, CharSequence... theText) {
        super(theText);
        this.locators = locators;
        this.locatorNames = (locators.size() == 1) ? locators.get(0).toString() : locators.toString();
    }

    @Step("{0} enters #theTextAsAString into #locatorNames")
    public <T extends Actor> void performAs(T theUser) {
        textValue().ifPresent(
                text -> resolveFor(theUser).sendKeys(theText)
        );
        if (getFollowedByKeys().length > 0) {
            resolveFor(theUser).sendKeys(getFollowedByKeys());
        } 
    }
}
