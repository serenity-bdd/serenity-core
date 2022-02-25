package net.serenitybdd.screenplay.targets;

import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.pages.ListOfWebElementFacades;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementFacadeImpl;
import net.serenitybdd.core.selectors.Selectors;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class XPathOrCssTarget extends SearchableTarget {

    private final String cssOrXPathSelector;

    public XPathOrCssTarget(String targetElementName, String cssOrXPathSelector, Optional<IFrame> iFrame, Optional<Duration> timeout) {
        super(targetElementName, iFrame, timeout);
        this.cssOrXPathSelector = cssOrXPathSelector;
    }

    public WebElementFacade resolveFor(PageObject page) {
        if (timeout.isPresent()) {
            return page.withTimeoutOf(timeout.get()).find(cssOrXPathSelector);
        } else {
            return page.findBy(cssOrXPathSelector);
        }
    }

    public ListOfWebElementFacades resolveAllFor(PageObject page) {
        if (timeout.isPresent()) {
            return new ListOfWebElementFacades(page.withTimeoutOf(timeout.get()).findAll(cssOrXPathSelector));
        } else {
            return new ListOfWebElementFacades(page.findAll(cssOrXPathSelector));
        }
    }

    @Override
    public WebElementFacade resolveFor(SearchContext searchContext) {
        return WebElementFacadeImpl.wrapWebElement(
                Serenity.getDriver(),
                searchContext.findElement(Selectors.xpathOrCssSelector(cssOrXPathSelector))
        );
    }

    @Override
    public ListOfWebElementFacades resolveAllFor(SearchContext searchContext) {
        List<WebElement> matchingElements = searchContext.findElements(Selectors.xpathOrCssSelector(cssOrXPathSelector));
        List<WebElementFacade> facades = matchingElements.stream()
                .map(element -> WebElementFacadeImpl.wrapWebElement(Serenity.getDriver(),element))
                .collect(Collectors.toList());
        return new ListOfWebElementFacades(facades);
    }

    public SearchableTarget of(String... parameters) {
        return new XPathOrCssTarget(instantiated(targetElementName, parameters),
                instantiated(cssOrXPathSelector, parameters),
                iFrame,
                timeout);
    }

    public XPathOrCssTarget called(String name) {
        return new XPathOrCssTarget(name, cssOrXPathSelector, iFrame, timeout);
    }

    public List<String> getCssOrXPathSelectors() {
        return Collections.singletonList(cssOrXPathSelector);
    }

    public String getCssOrXPathSelector() {
        return cssOrXPathSelector;
    }

    @Override
    public Target waitingForNoMoreThan(Duration timeout) {
        return new XPathOrCssTarget(targetElementName, cssOrXPathSelector, iFrame, Optional.ofNullable(timeout));
    }

    @Override
    public List<By> selectors(WebDriver driver) {
        return Collections.singletonList(Selectors.xpathOrCssSelector(cssOrXPathSelector));
    }

    private String instantiated(String cssOrXPathSelector, String[] parameters) {
        return new TargetSelectorWithVariables(cssOrXPathSelector).resolvedWith(parameters);
    }
}
