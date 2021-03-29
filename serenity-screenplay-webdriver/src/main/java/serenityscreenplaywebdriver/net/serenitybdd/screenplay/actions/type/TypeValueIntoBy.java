package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.type;

import serenitymodel.net.serenitybdd.core.collect.NewList;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.WebElementLocator;
import serenitymodel.net.thucydides.core.annotations.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class TypeValueIntoBy extends TypeValue {

    private final List<By> locators;

    protected WebElement resolveFor(Actor theUser) {
        return WebElementLocator.forLocators(locators).andActor(theUser);
    }

    public TypeValueIntoBy(List<By> locators, CharSequence... theText) {
        super(theText);
        this.locators = NewList.copyOf(locators);
    }

    @Step("{0} enters #theTextAsAString into #locators")
    public <T extends Actor> void performAs(T theUser) {
        WebElement element = resolveFor(theUser);

        element.sendKeys(theText);
        if (getFollowedByKeys().length > 0) {
            element.sendKeys(getFollowedByKeys());
        }
    }
}
