package net.serenitybdd.screenplay.targets;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;

import java.util.List;

public class XPathOrCssTarget extends Target {

    private final String cssOrXPathSelector;

    public XPathOrCssTarget(String targetElementName, String cssOrXPathSelector, IFrame iFrame) {
        super(targetElementName, iFrame);
        this.cssOrXPathSelector = cssOrXPathSelector;
    }

    public WebElementFacade resolveFor(Actor actor) {
        return TargetResolver.create(actor, this).findBy(cssOrXPathSelector);
    }

    public List<WebElementFacade> resolveAllFor(Actor actor) {
        return TargetResolver.create(actor, this).findAll(cssOrXPathSelector);
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
