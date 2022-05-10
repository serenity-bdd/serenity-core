package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class EnterValueIntoBy extends EnterValue {

    private final List<By> locators;
    private final String locatorNames;

    protected WebElement resolveFor(Actor theUser) {
        return WebElementLocator.forLocators(locators).andActor(theUser);
    }

    public EnterValueIntoBy(List<By> locators, CharSequence... theText) {
        super(theText);
        this.locators = NewList.copyOf(locators);
        this.locatorNames = (locators.size() == 1) ? locators.get(0).toString() : locators.toString();
    }

    @Step("{0} enters #theTextAsAString into #locatorNames")
    public <T extends Actor> void performAs(T theUser) {
        textValue().ifPresent(
                text -> {
                    resolveFor(theUser).clear();
                    resolveFor(theUser).sendKeys(text);
                }
        );
        if (getFollowedByKeys() != null && getFollowedByKeys().length > 0) {
            resolveFor(theUser).sendKeys(getFollowedByKeys());
        }
    }
}
