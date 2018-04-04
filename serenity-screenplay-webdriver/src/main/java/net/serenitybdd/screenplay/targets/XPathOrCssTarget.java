package net.serenitybdd.screenplay.targets;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

import java.util.List;
import java.util.Optional;

public class XPathOrCssTarget extends Target {

    private final String cssOrXPathSelector;

    public XPathOrCssTarget(String targetElementName, String cssOrXPathSelector, Optional<IFrame> iFrame) {
        super(targetElementName, iFrame);
        this.cssOrXPathSelector = cssOrXPathSelector;
    }

    public WebElementFacade resolveFor(Actor actor) {
        return TargetResolver.create(BrowseTheWeb.as(actor).getDriver(), this).findBy(cssOrXPathSelector);
    }

    public List<WebElementFacade> resolveAllFor(Actor actor) {
        return TargetResolver.create(BrowseTheWeb.as(actor).getDriver(), this).findAll(cssOrXPathSelector);
    }

    public Target of(String... parameters) {
        return new XPathOrCssTarget(instantiated(targetElementName, parameters),
                                    instantiated(cssOrXPathSelector, parameters), iFrame);
    }

    public Target called(String name) {
        return new XPathOrCssTarget(name, cssOrXPathSelector, iFrame);
    }

    public String getCssOrXPathSelector() {
        return cssOrXPathSelector;
    }

    private String instantiated(String cssOrXPathSelector, String[] parameters) {
        return new TargetSelectorWithVariables(cssOrXPathSelector).resolvedWith(parameters);
    }
}
