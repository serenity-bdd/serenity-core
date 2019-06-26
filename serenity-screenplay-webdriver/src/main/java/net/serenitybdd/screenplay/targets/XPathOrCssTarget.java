package net.serenitybdd.screenplay.targets;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class XPathOrCssTarget extends Target {

    private final String cssOrXPathSelector;

    public XPathOrCssTarget(String targetElementName, String cssOrXPathSelector, Optional<IFrame> iFrame, Optional<Duration> timeout) {
        super(targetElementName, iFrame, timeout);
        this.cssOrXPathSelector = cssOrXPathSelector;
    }

    public WebElementFacade resolveFor(Actor actor) {
        TargetResolver resolver = TargetResolver.create(BrowseTheWeb.as(actor).getDriver(), this);
        if (timeout.isPresent()) {
            return resolver.withTimeoutOf(timeout.get()).find(cssOrXPathSelector);
        } else {
            return resolver.findBy(cssOrXPathSelector);
        }
//        return TargetResolver.create(BrowseTheWeb.as(actor).getDriver(), this).findBy(cssOrXPathSelector);
    }

    public List<WebElementFacade> resolveAllFor(Actor actor) {
        TargetResolver resolver = TargetResolver.create(BrowseTheWeb.as(actor).getDriver(), this);
        if (timeout.isPresent()) {
            return resolver.withTimeoutOf(timeout.get()).findAll(cssOrXPathSelector);
        } else {
            return resolver.findAll(cssOrXPathSelector);
        }
//        return TargetResolver.create(BrowseTheWeb.as(actor).getDriver(), this).findAll(cssOrXPathSelector);
    }

    public Target of(String... parameters) {
        return new XPathOrCssTarget(instantiated(targetElementName, parameters),
                                    instantiated(cssOrXPathSelector, parameters),
                                    iFrame,
                                    timeout);
    }

    public Target called(String name) {
        return new XPathOrCssTarget(name, cssOrXPathSelector, iFrame, timeout);
    }

    public String getCssOrXPathSelector() {
        return cssOrXPathSelector;
    }

    @Override
    public Target waitingForNoMoreThan(Duration timeout) {
        return new XPathOrCssTarget(targetElementName, cssOrXPathSelector, iFrame, Optional.ofNullable(timeout));
    }

    private String instantiated(String cssOrXPathSelector, String[] parameters) {
        return new TargetSelectorWithVariables(cssOrXPathSelector).resolvedWith(parameters);
    }
}
