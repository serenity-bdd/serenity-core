package net.serenitybdd.screenplay.targets;

import net.serenitybdd.core.pages.ListOfWebElementFacades;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.webdriver.RemoteDriver;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.webdriver.ThucydidesConfigurationException;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ByTarget extends SearchableTarget {

    private By locator;
    private By androidLocator;
    private By iosLocator;

    public ByTarget(String targetElementName, By locator, Optional<IFrame> iFrame) {
        super(targetElementName, iFrame);
        this.locator = locator;
    }

    public ByTarget(String targetElementName, By androidLocator, By iosLocator, Optional<IFrame> iFrame) {
        super(targetElementName, iFrame);
        this.androidLocator = androidLocator;
        this.iosLocator = iosLocator;
    }

    public ByTarget(String targetElementName, By androidLocator, By iosLocator, Optional<IFrame> iFrame, Optional<Duration> timeout) {
        super(targetElementName, iFrame, timeout);
        this.androidLocator = androidLocator;
        this.iosLocator = iosLocator;
    }


    protected ByTarget(String targetElementName, By locator, By androidLocator, By iosLocator, Optional<IFrame> iFrame, Optional<Duration> timeout) {
        super(targetElementName, iFrame, timeout);
        this.locator = locator;
        this.androidLocator = androidLocator;
        this.iosLocator = iosLocator;
    }


    public WebElementFacade resolveFor(PageObject page) {
        if (timeout.isPresent()) {
            return page.withTimeoutOf(timeout.get()).find(getLocatorForPlatform(page.getDriver()));
        } else {
            return page.find(getLocatorForPlatform(page.getDriver()));
        }
    }

    public ListOfWebElementFacades resolveAllFor(PageObject page) {
        if (timeout.isPresent()) {
            return new ListOfWebElementFacades(page.withTimeoutOf(timeout.get()).findAll(getLocatorForPlatform(page.getDriver())));
        } else {
            return new ListOfWebElementFacades(page.findAll(getLocatorForPlatform(page.getDriver())));
        }
    }

    public SearchableTarget of(String... parameters) {
        throw new UnsupportedOperationException("The of() method is not supported for By-type Targets");
    }

    private By getLocatorForPlatform(WebDriver driver) {
        if (null != this.androidLocator && null != this.iosLocator) {
            String platform;
            try {
                platform = (RemoteDriver.isStubbed(driver)) ?
                        "IOS" :
                        RemoteDriver.of(driver)
                                .getCapabilities().getCapability("platformName")
                                .toString().toUpperCase();
            } catch (Exception e) {
                throw new ThucydidesConfigurationException(String.format(
                        "The configured driver '%s' does not support Cross Platform Mobile targets",
                        driver
                ), e);
            }
            if (platform.equals("ANDROID")) {
                return this.androidLocator;
            } else if (platform.equals("IOS")) {
                return this.iosLocator;
            } else {
                throw new ThucydidesConfigurationException(String.format(
                        "'%s' is not a valid platform for Cross Platform Mobile targets", platform
                ));
            }
        }
        return this.locator;
    }

    @Override
    public String getCssOrXPathSelector() {
        throw new UnsupportedOperationException("The getCssOrXPathSelector() method is not supported for By-type Targets");
    }

    @Override
    public Target waitingForNoMoreThan(Duration timeout) {
        return new ByTarget(targetElementName, locator, androidLocator, iosLocator, iFrame, Optional.ofNullable(timeout));
    }

    @Override
    public List<By> selectors(WebDriver driver) {
        return Collections.singletonList(getLocatorForPlatform(driver));
    }

    public ByTarget called(String name) {
        return new ByTarget(name, locator, androidLocator, iosLocator, iFrame, timeout);
    }

    @Override
    public List<String> getCssOrXPathSelectors() {
        throw new UnsupportedOperationException("The getCssOrXPathSelector() method is not supported for By Targets");
    }
}
