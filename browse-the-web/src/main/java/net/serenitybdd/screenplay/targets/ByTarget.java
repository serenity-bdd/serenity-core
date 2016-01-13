package net.serenitybdd.screenplay.targets;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;

import java.util.List;

public class ByTarget extends Target {

    private final By locator;

    public ByTarget(String targetElementName, By locator) {
        super(targetElementName);
        this.locator = locator;
    }

    public WebElementFacade resolveFor(Actor actor) {
        TargetResolver resolver = new TargetResolver(BrowseTheWeb.as(actor).getDriver());
        return resolver.find(locator);
    }

    public List<WebElementFacade> resolveAllFor(Actor actor) {
        TargetResolver resolver = new TargetResolver(BrowseTheWeb.as(actor).getDriver());
        return resolver.findAll(locator);
    }

    public ByTarget called(String name) {
        return new ByTarget(name, locator);
    }
}
