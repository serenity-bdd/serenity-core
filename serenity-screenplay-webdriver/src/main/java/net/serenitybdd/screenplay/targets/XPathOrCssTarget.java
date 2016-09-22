package net.serenitybdd.screenplay.targets;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

import java.util.List;

public class XPathOrCssTarget extends Target {

    private final String cssOrXPathSelector;

    public XPathOrCssTarget(String targetElementName, String cssOrXPathSelector, IFrame iFrame) {
        super(targetElementName, iFrame);
        this.cssOrXPathSelector = cssOrXPathSelector;
    }

    public WebElementFacade resolveFor(Actor theActor) {
        TargetResolver resolver = TargetResolver.switchIFrameIfRequired(BrowseTheWeb.as(theActor).getDriver(), this);
        WebElementFacade resolvedTarget = resolver.findBy(cssOrXPathSelector);
        return resolvedTarget;
    }

    public List<WebElementFacade> resolveAllFor(Actor theActor) {
        TargetResolver resolver = TargetResolver.switchIFrameIfRequired(BrowseTheWeb.as(theActor).getDriver(), this);
        return resolver.findAll(cssOrXPathSelector);
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

    @Override
    public IFrame getIFrame() {
        return iFrame;
    }

}
