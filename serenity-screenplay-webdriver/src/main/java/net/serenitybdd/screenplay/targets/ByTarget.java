package net.serenitybdd.screenplay.targets;

import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.pages.ListOfWebElementFacades;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementFacadeImpl;
import net.serenitybdd.core.selectors.Selectors;
import net.serenitybdd.core.webdriver.RemoteDriver;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.webdriver.ThucydidesConfigurationException;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ByTarget extends SearchableTarget implements HasByLocator {

    private By locator;

    public ByTarget(String targetElementName, By locator, Optional<IFrame> iFrame) {
        super(targetElementName, iFrame);
        this.locator = locator;
    }

    public ByTarget(String targetElementName, Optional<IFrame> iFrame, Optional<Duration> timeout) {
        super(targetElementName, iFrame, timeout);
    }


    protected ByTarget(String targetElementName, By locator, Optional<IFrame> iFrame, Optional<Duration> timeout) {
        super(targetElementName, iFrame, timeout);
        this.locator = locator;
    }

    public WebElementFacade resolveFor(PageObject page) {
        if (timeout.isPresent()) {
            return page.withTimeoutOf(timeout.get()).find(locator);
        } else {
            return page.find(locator);
        }
    }

    public ListOfWebElementFacades resolveAllFor(PageObject page) {
        if (timeout.isPresent()) {
            return new ListOfWebElementFacades(page.withTimeoutOf(timeout.get()).findAll(locator));
        } else {
            return new ListOfWebElementFacades(page.findAll(locator));
        }
    }


    @Override
    public WebElementFacade resolveFor(SearchContext searchContext) {
        return WebElementFacadeImpl.wrapWebElement(
                Serenity.getDriver(),
                searchContext.findElement(locator)
        );
    }

    @Override
    public ListOfWebElementFacades resolveAllFor(SearchContext searchContext) {
        List<WebElement> matchingElements = searchContext.findElements(locator);
        return WebElementFacadeImpl.fromWebElements(matchingElements);
    }

    public SearchableTarget of(String... parameters) {
        throw new UnsupportedOperationException("The of() method is not supported for By-type Targets");
    }

    @Override
    public String getCssOrXPathSelector() {
        throw new UnsupportedOperationException("The getCssOrXPathSelector() method is not supported for By-type Targets");
    }

    @Override
    public Target waitingForNoMoreThan(Duration timeout) {
        return new ByTarget(targetElementName, locator, iFrame, Optional.ofNullable(timeout));
    }

    @Override
    public List<By> selectors(WebDriver driver) {
        return Collections.singletonList(locator);
    }

    public ByTarget called(String name) {
        return new ByTarget(name, locator, iFrame, timeout);
    }

    @Override
    public List<String> getCssOrXPathSelectors() {
        throw new UnsupportedOperationException("The getCssOrXPathSelector() method is not supported for By Targets");
    }

    @Override
    public By getLocator() {
        return locator;
    }
}
