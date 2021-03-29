package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenitymodel.net.serenitybdd.core.collect.NewList;
import serenitycore.net.serenitybdd.core.pages.WebElementFacade;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;

import java.util.List;

import static serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.EnsureFieldVisible.ensureThat;

public abstract class ByAction implements Interaction, Resolvable {
    protected final List<By> locators;

    public ByAction(By... locators) {
        this.locators = NewList.copyOf(locators);
    }

    public WebElementFacade resolveFor(Actor theActor) {
        WebElementFacade element = null;
        for(By locator : locators) {
            element = (element == null) ? BrowseTheWeb.as(theActor).find(locator) : element.find(locator);
        }
        ensureThat(theActor).canSee(element);
        return element;
    }
}
