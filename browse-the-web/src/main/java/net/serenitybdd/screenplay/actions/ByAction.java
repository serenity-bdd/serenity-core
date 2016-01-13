package net.serenitybdd.screenplay.actions;

import com.google.common.collect.ImmutableList;
import net.serenitybdd.screenplay.Action;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public abstract class ByAction  implements Action {
    protected final List<By> locators;

    public ByAction(By... locators) {
        this.locators = ImmutableList.copyOf(locators);
    }

    protected WebElement resolveFor(Actor theUser) {
        WebElement element = null;
        for(By locator : locators) {
            element = (element == null) ? BrowseTheWeb.as(theUser).find(locator) : element.findElement(locator);
        }
        return element;
    }
}
