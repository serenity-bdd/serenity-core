package net.serenitybdd.screenplay.actions;

import net.serenitybdd.model.collect.NewList;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class WebElementLocator {

    private final List<By> locators;

    public WebElementLocator(List<By> locators) {
        this.locators = NewList.copyOf(locators);
    }

    public static WebElementLocator forLocators(List<By> locators) {
        return new WebElementLocator(locators);
    }

    public WebElement andActor(Actor theUser) {
        WebElement element = null;
        for(By locator : locators) {
            element = (element == null) ? BrowseTheWeb.as(theUser).find(locator) : element.findElement(locator);
        }
        return element;
    }
}
