package net.serenitybdd.core.pages;

import net.thucydides.core.annotations.locators.MethodTiming;
import net.thucydides.core.annotations.locators.WithConfigurableTimeout;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.ConfigurableTimeouts;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WebElementResolverByElementLocator extends WebElementResolver {
    private final ElementLocator locator;
    private final long implicitTimeoutInMilliseconds;

    public WebElementResolverByElementLocator(ElementLocator locator, long implicitTimeoutInMilliseconds) {
        this.locator = locator;
        this.implicitTimeoutInMilliseconds = implicitTimeoutInMilliseconds;
    }

    @Override
    public WebElement resolveForDriver(WebDriver driver) {
        if (locator == null) { return null; }

        WebElement resolvedELement = getLocatorWithDriver(driver).findElement();
        ensureVisibilityOf(resolvedELement);
        return resolvedELement;
    }

    @Override
    public List<WebElement> resolveAllForDriver(WebDriver driver) {
        if (locator == null) { return Collections.emptyList(); }

        return getLocatorWithDriver(driver).findElements();
    }

    private void ensureVisibilityOf(WebElement resolvedELement) {
        if (resolvedELement == null) {
            throw new ElementNotVisibleException(locator.toString());
        }
    }

    private ElementLocator getLocatorWithDriver(WebDriver driver) {
        if ((locator instanceof WithConfigurableTimeout) && (driver instanceof ConfigurableTimeouts)) {
            ((WithConfigurableTimeout) locator).setTimeOutInSeconds((int) getLocatorTimeout());
        }
        return locator;
    }

    private long getLocatorTimeout() {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended() || (MethodTiming.forThisThread().isInQuickMethod())) {
            return 0;
        } else {
            return TimeUnit.SECONDS.convert(implicitTimeoutInMilliseconds, TimeUnit.MILLISECONDS);
        }
    }
}
