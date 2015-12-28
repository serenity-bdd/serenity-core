package net.serenitybdd.screenplay.targets;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

import java.util.List;

public class Target {

    private final String targetElementName;
    private final String cssOrXPathSelector;

    public Target(String targetElementName, String cssOrXPathSelector) {
        this.targetElementName = targetElementName;
        this.cssOrXPathSelector = cssOrXPathSelector;
    }

    public String getTargetElementName() {
        return targetElementName;
    }

    public String getCssOrXPathSelector() {
        return cssOrXPathSelector;
    }

    @Override
    public String toString() {
        return targetElementName;
    }

    public static TargetBuilder the(String targetElementName) {
        return new TargetBuilder(targetElementName);
    }

    public WebElementFacade resolveFor(Actor actor) {
        TargetResolver resolver = new TargetResolver(BrowseTheWeb.as(actor).getDriver());
        return resolver.findBy(cssOrXPathSelector);
    }

    public List<WebElementFacade> resolveAllFor(Actor actor) {
        TargetResolver resolver = new TargetResolver(BrowseTheWeb.as(actor).getDriver());
        return resolver.findAll(cssOrXPathSelector);
    }

    public Target of(String... parameters) {
        return new Target(targetElementName, instantiated(cssOrXPathSelector, parameters));
    }

    public Target called(String name) {
        return new Target(name, cssOrXPathSelector);
    }

    private String instantiated(String cssOrXPathSelector, String[] parameters) {
        return new TargetSelectorWithVariables(cssOrXPathSelector).resolvedWith(parameters);
    }
}
