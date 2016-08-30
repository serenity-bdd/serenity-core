package net.serenitybdd.screenplay.actions;

import com.google.common.collect.ImmutableList;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static net.serenitybdd.screenplay.targets.EnsureFieldVisible.ensureThat;

public abstract class ByAction  implements Interaction {
    protected final List<By> locators;

    public ByAction(By... locators) {
        this.locators = ImmutableList.copyOf(locators);
    }

    protected WebElement resolveFor(Actor theActor) {
        WebElementFacade element = null;
        for(By locator : locators) {
            element = (element == null) ? BrowseTheWeb.as(theActor).find(locator) : element.find(locator);
        }
        ensureThat(theActor).canSee(element);
        return element;
    }
}
