package net.serenitybdd.core.pages;

import com.google.common.base.Splitter;
import io.appium.java_client.*;
import net.serenitybdd.core.time.InternalSystemClock;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.locators.MethodTiming;
import net.thucydides.core.annotations.locators.WithConfigurableTimeout;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.ConfigurableTimeouts;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.exceptions.*;
import net.thucydides.core.webdriver.javascript.JavascriptExecutorFacade;
import net.thucydides.core.webdriver.stubs.WebElementFacadeStub;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.interactions.internal.Locatable;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.time.Duration;
import java.util.stream.Collectors;

import static net.serenitybdd.core.pages.WebElementExpectations.*;
import static net.serenitybdd.core.selectors.Selectors.isXPath;


/**
 * A proxy class for a web element, providing some more methods.
 */
public class WebElementFacadeImpl implements WebElementFacade, net.thucydides.core.pages.WebElementFacade {

    private final WebElement webElement;
    private final WebDriver driver;
    private final long implicitTimeoutInMilliseconds;
    private final long waitForTimeoutInMilliseconds;
    private static final int WAIT_FOR_ELEMENT_PAUSE_LENGTH = 100;
    private final Sleeper sleeper;
    private final Clock webdriverClock;
    private final By bySelector;
    private JavascriptExecutorFacade javascriptExecutorFacade;
    private InternalSystemClock clock = new InternalSystemClock();
    private final EnvironmentVariables environmentVariables;
    private String foundBy;

    private ElementLocator locator;
    private WebElement resolvedELement;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebElementFacadeImpl.class);

    public WebElementFacadeImpl(final WebDriver driver,
                                   final ElementLocator locator,
                                   final WebElement webElement,
                                   final long implicitTimeoutInMilliseconds,
                                   final long waitForTimeoutInMilliseconds,
                                   final By bySelector) {
        this.webElement = webElement;
        this.driver = driver;
        this.locator = locator;
        this.bySelector = bySelector;
        this.webdriverClock = new org.openqa.selenium.support.ui.SystemClock();
        this.sleeper = Sleeper.SYSTEM_SLEEPER;
        this.javascriptExecutorFacade = new JavascriptExecutorFacade(driver);
        this.environmentVariables = Injectors.getInjector().getProvider(EnvironmentVariables.class).get();
        this.implicitTimeoutInMilliseconds = implicitTimeoutInMilliseconds;
        this.waitForTimeoutInMilliseconds = (waitForTimeoutInMilliseconds >= 0) ? waitForTimeoutInMilliseconds : defaultWaitForTimeout();
    }

    public WebElementFacadeImpl(final WebDriver driver,
                                final ElementLocator locator,
                                final WebElement webElement,
                                final long implicitTimeoutInMilliseconds,
                                final long waitForTimeoutInMilliseconds) {
        this.webElement = webElement;
        this.driver = driver;
        this.locator = locator;
        this.bySelector = null;
        this.webdriverClock = new org.openqa.selenium.support.ui.SystemClock();
        this.sleeper = Sleeper.SYSTEM_SLEEPER;
        this.javascriptExecutorFacade = new JavascriptExecutorFacade(driver);
        this.environmentVariables = Injectors.getInjector().getProvider(EnvironmentVariables.class).get();
        this.implicitTimeoutInMilliseconds = implicitTimeoutInMilliseconds;
        this.waitForTimeoutInMilliseconds = (waitForTimeoutInMilliseconds >= 0) ? waitForTimeoutInMilliseconds : defaultWaitForTimeout();
    }

    public WebElementFacadeImpl(final WebDriver driver,
                                   final ElementLocator locator,
                                   final WebElement webElement,
                                   final long implicitTimeoutInMilliseconds) {
        this(driver, locator, webElement, implicitTimeoutInMilliseconds, implicitTimeoutInMilliseconds);
    }

    public WebElementFacadeImpl(WebDriver driver,
                                ElementLocator locator,
                                WebElement webElement,
                                WebElement resolvedELement,
                                By bySelector,
                                long timeoutInMilliseconds,
                                long waitForTimeoutInMilliseconds) {
        this.webElement = webElement;
        this.resolvedELement = resolvedELement;
        this.driver = driver;
        this.locator = locator;
        this.bySelector = bySelector;
        this.webdriverClock = new org.openqa.selenium.support.ui.SystemClock();
        this.sleeper = Sleeper.SYSTEM_SLEEPER;
        this.javascriptExecutorFacade = new JavascriptExecutorFacade(driver);
        this.environmentVariables = Injectors.getInjector().getProvider(EnvironmentVariables.class).get();
        this.implicitTimeoutInMilliseconds = timeoutInMilliseconds;
        this.waitForTimeoutInMilliseconds = (waitForTimeoutInMilliseconds >= 0) ? waitForTimeoutInMilliseconds : defaultWaitForTimeout();

    }


    private long defaultWaitForTimeout() {
        return ThucydidesSystemProperty.WEBDRIVER_WAIT_FOR_TIMEOUT.integerFrom(environmentVariables,
                (int) DefaultTimeouts.DEFAULT_WAIT_FOR_TIMEOUT.toMillis());
    }

    private WebElementFacadeImpl copy() {
        return new WebElementFacadeImpl(driver, locator, webElement, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds);
    }

    public WebElementFacadeImpl(final WebDriver driver,
                                final ElementLocator locator,
                                final long implicitTimeoutInMilliseconds) {
        this(driver, locator, null, implicitTimeoutInMilliseconds, implicitTimeoutInMilliseconds);
    }

    public WebElementFacadeImpl(final WebDriver driver,
                                final ElementLocator locator,
                                final long implicitTimeoutInMilliseconds,
                                final long waitForTimeoutInMilliseconds) {
        this(driver, locator, null, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds);
    }

    public static <T extends WebElementFacade> T wrapWebElement(final WebDriver driver,
                                                                final WebElement element,
                                                                final long timeoutInMilliseconds,
                                                                final long waitForTimeoutInMilliseconds) {
        return (T) new WebElementFacadeImpl(driver, null, element, timeoutInMilliseconds, waitForTimeoutInMilliseconds)
                       .foundBy("<Undefined web element>");
    }

    public static <T extends WebElementFacade> T wrapWebElement(final WebDriver driver,
                                                                final WebElement element,
                                                                final long timeoutInMilliseconds,
                                                                final long waitForTimeoutInMilliseconds,
                                                                final String foundBy) {
        return (T) new WebElementFacadeImpl(driver, null, element, timeoutInMilliseconds, waitForTimeoutInMilliseconds)
                       .foundBy(foundBy);
    }

    public  static <T extends WebElementFacade> T  wrapWebElement(WebDriver driver,
                                                                  WebElement resolvedELement,
                                                                  WebElement element,
                                                                  By bySelector,
                                                                  ElementLocator locator,
                                                                  long timeoutInMilliseconds,
                                                                  long waitForTimeoutInMilliseconds,
                                                                  String foundBy) {
        return (T) new WebElementFacadeImpl(driver, locator, element, resolvedELement, bySelector, timeoutInMilliseconds, waitForTimeoutInMilliseconds)
                .foundBy(foundBy);
    }

    public static <T extends WebElementFacade> T wrapWebElement(final WebDriver driver,
                                                                final By bySelector,
                                                                final long timeoutInMilliseconds,
                                                                final long waitForTimeoutInMilliseconds,
                                                                final String foundBy) {
        return (T) new WebElementFacadeImpl(driver, null, null, timeoutInMilliseconds, waitForTimeoutInMilliseconds, bySelector)
                .foundBy(foundBy);
    }

    public static <T extends WebElementFacade> T wrapWebElement(final WebDriver driver,
                                                                final WebElement element,
                                                                final long timeout) {
        return (T) new WebElementFacadeImpl(driver, null, element, timeout, timeout).foundBy(element.toString());

    }

    private WebElementResolver getElementResolver() {
        if (webElement != null) {
            return WebElementResolver.forWebElement(webElement);
        }
        if (bySelector != null) {
            return WebElementResolver.by(bySelector);
        }
        return WebElementResolver.byLocator(locator).withImplicitTimeout(implicitTimeoutInMilliseconds);
    }

    protected WebElement getElement() {
        if (driverIsDisabled()) { return new WebElementFacadeStub();}

        if (resolvedELement != null) { return resolvedELement; }

        return (resolvedELement = getElementResolver().resolveForDriver(driver));
    }

    protected JavascriptExecutorFacade getJavascriptExecutorFacade() {
        return javascriptExecutorFacade;
    }

    protected InternalSystemClock getClock() {
        return clock;
    }


    @Override
    public WebElementFacade then(String xpathOrCssSelector) {
        return findBy(xpathOrCssSelector);
    }

    @Override
    public <T extends WebElementFacade> T findBy(String xpathOrCssSelector) {
        logIfVerbose("findBy " + xpathOrCssSelector);
        WebElement nestedElement;
        if (driverIsDisabled()) {
            nestedElement = this;
        } else if (isXPath(xpathOrCssSelector)) {
            nestedElement = getElement().findElement((By
                    .xpath(xpathOrCssSelector)));
        } else {
            nestedElement = getElement().findElement((By
                    .cssSelector(xpathOrCssSelector)));
        }

        return wrapWebElement(driver, nestedElement, timeoutInMilliseconds(), waitForTimeoutInMilliseconds,
                              "element located by " + xpathOrCssSelector);
    }


    public long timeoutInMilliseconds() {
        if (driver instanceof WebDriverFacade) {
            return ((WebDriverFacade) driver).getCurrentImplicitTimeout().toMillis();
        } else {
            return implicitTimeoutInMilliseconds;
        }

    }

    @Override
    public List<WebElementFacade> thenFindAll(String xpathOrCssSelector) {
        logIfVerbose("findAll " + xpathOrCssSelector);
        if (driverIsDisabled()) { return new ArrayList<>(); }

        List<WebElement> nestedElements;
        if (isXPath(xpathOrCssSelector)) {
            nestedElements = findElements((By.xpath(xpathOrCssSelector)));
        } else {
            nestedElements = findElements((By.cssSelector(xpathOrCssSelector)));
        }

        return webElementFacadesFrom(nestedElements);
    }

    private List<WebElementFacade> webElementFacadesFrom(List<WebElement> nestedElements) {
        List<WebElementFacade> results = new ArrayList<>();
        for (WebElement element : nestedElements) {
            results.add(wrapWebElement(driver, element, timeoutInMilliseconds(), waitForTimeoutInMilliseconds, element.toString()));
        }
        return results;
    }

    @Override
    public WebElementFacade findBy(By selector) {
        logIfVerbose("findBy " + selector);

        if (driverIsDisabled()) { return this; }

        WebElement nestedElement = getElement().findElement(selector);
        return wrapWebElement(driver, nestedElement, timeoutInMilliseconds(), waitForTimeoutInMilliseconds,
                              "element located by " + selector.toString());
    }

    @Override
    public WebElementFacade find(By bySelector) {
        return findBy(bySelector);
    }

    @Override
    public WebElementFacade then(By bySelector) {
        return findBy(bySelector);
    }

    @Override
    public String getAttribute(String name) {
        if (driverIsDisabled()) { return ""; }

        return getElement().getAttribute(name);
    }

    @Override
    public List<WebElementFacade> thenFindAll(By selector) {
        logIfVerbose("findAll " + selector);
        if (driverIsDisabled()) { return new ArrayList<>(); }

        List<WebElement> nestedElements = findElements(selector);
        return webElementFacadesFrom(nestedElements);
    }

    @Override
    public WebElement findElementByAccessibilityId(String id) {
        if (driverIsDisabled()) { return this; }

        return ((FindsByAccessibilityId) getElement()).findElementByAccessibilityId(id);
    }

    @Override
    public List<WebElement> findElementsByAccessibilityId(String id) {
        if (driverIsDisabled()) { return new ArrayList<>(); }

        return ((FindsByAccessibilityId) getElement()).findElementsByAccessibilityId(id);
    }

    @Override
    public WebElement findElementByAndroidUIAutomator(String using) {
        if (driverIsDisabled()) { return this; }

        return ((FindsByAndroidUIAutomator) getElement()).findElementByAndroidUIAutomator(using);
    }

    @Override
    public List<WebElement> findElementsByAndroidUIAutomator(String using) {
        if (driverIsDisabled()) { return new ArrayList<>(); }

        return ((FindsByAndroidUIAutomator) getElement()).findElementsByAndroidUIAutomator(using);
    }

    @Override
    public WebElement findElementByIosUIAutomation(String using) {
        if (driverIsDisabled()) { return this; }

        return ((FindsByIosUIAutomation) getElement()).findElementByIosUIAutomation(using);
    }

    @Override
    public List<WebElement> findElementsByIosUIAutomation(String using) {
        if (driverIsDisabled()) { return new ArrayList<>(); }

        return ((FindsByIosUIAutomation) getElement()).findElementsByIosUIAutomation(using);
    }

    @Override
    public long getImplicitTimeoutInMilliseconds() {
        return implicitTimeoutInMilliseconds;
    }

    public Duration getImplicitTimeout() {
        return ((ConfigurableTimeouts) driver).getCurrentImplicitTimeout();
    }


    @Override
    public WebElementFacade withTimeoutOf(int timeout, TimeUnit unit) {
        return wrapWebElement(driver,
                resolvedELement,
                webElement,
                bySelector,
                locator,
                implicitTimeoutInMilliseconds,
                TimeUnit.MILLISECONDS.convert(timeout, unit),
                foundBy);
    }

    /**
     * Is this web element present and visible on the screen
     * This method will not throw an exception if the element is not on the screen at all.
     * If the element is not visible, the method will wait a bit to see if it appears later on.
     */
    @Override
    public boolean isVisible() {

        if (driverIsDisabled()) { return false; }

        try {
            WebElement element = getElement();
            return (element != null) && (element.isDisplayed());
        } catch (ElementNotVisibleException e) {
            return false;
        } catch (NoSuchElementException e) {
            return false;
        } catch (StaleElementReferenceException se) {
            return false;
        }
    }

    /**
     * Convenience method to chain method calls more fluently.
     */
    @Override
    public WebElementFacade and() {
        return this;
    }

    /**
     * Convenience method to chain method calls more fluently.
     */
    @Override
    public WebElementFacade then() {
        return this;
    }

    /**
     * Is this web element present and visible on the screen
     * This method will not throw an exception if the element is not on the screen at all.
     * The method will fail immediately if the element is not visible on the screen.
     * There is a little black magic going on here - the web element class will detect if it is being called
     * by a method called "isCurrently*" and, if so, fail immediately without waiting as it would normally do.
     */
    @Override
    public boolean isCurrentlyVisible() {
        return isVisible();
    }

    @Override
    public boolean isCurrentlyEnabled() {
        if (driverIsDisabled()) { return false; }

        try {
            return getElement().isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        } catch (StaleElementReferenceException se) {
            return false;
        }
    }

    /**
     * Checks whether a web element is visible.
     * Throws an AssertionError if the element is not rendered.
     */
    @Override
    public void shouldBeVisible() {
        if (!isVisible()) {
            failWithMessage("Element should be visible");
        }
    }

    /**
     * Checks whether a web element is visible.
     * Throws an AssertionError if the element is not rendered.
     */
    @Override
    public void shouldBeCurrentlyVisible() {
        if (!isCurrentlyVisible()) {
            failWithMessage("Element should be visible");
        }
    }

    /**
     * Checks whether a web element is not visible.
     * Throws an AssertionError if the element is not rendered.
     */
    @Override
    public void shouldNotBeVisible() {
        if (isCurrentlyVisible()) {
            failWithMessage("Element should not be visible");
        }
    }

    /**
     * Checks whether a web element is not visible straight away.
     * Throws an AssertionError if the element is not rendered.
     */
    @Override
    public void shouldNotBeCurrentlyVisible() {
        if (isCurrentlyVisible()) {
            failWithMessage("Element should not be visible");
        }
    }

    /**
     * Does this element currently have the focus.
     */
    @Override
    public boolean hasFocus() {
        if (driverIsDisabled()) { return false; }

        JavascriptExecutorFacade js = new JavascriptExecutorFacade(driver);
        WebElement activeElement = (WebElement) js.executeScript("return window.document.activeElement");
        return getElement().equals(activeElement);
    }

    /**
     * Does this element contain a given text?
     */
    @Override
    public boolean containsText(final String value) {
        if (driverIsDisabled()) { return false; }

        WebElement element = getElement();
        return ((element != null) && (element.getText().contains(value)));
    }

    @Override
    public boolean containsValue(String value) {
        if (driverIsDisabled()) { return false; }

        WebElement element = getElement();
        return ((element != null) && (element.getAttribute("value").contains(value)));
    }


    /**
     * Does this element exactly match  given text?
     */
    @Override
    public boolean containsOnlyText(final String value) {
        if (driverIsDisabled()) { return false; }

        WebElement element = getElement();
        return ((element != null) && (element.getText().equals(value)));
    }

    /**
     * Does this dropdown contain the specified value.
     */
    @Override
    public boolean containsSelectOption(final String value) {
        if (driverIsDisabled()) { return false; }

        return getSelectOptions().contains(value);
    }

    @Override
    public List<String> getSelectOptions() {
        if (driverIsDisabled()) { return new ArrayList<>(); }

        if (getElement() != null) {
            return findElements(By.tagName("option")).stream()
                                                     .map(WebElement::getText)
                                                     .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public ElementLocator getLocator() {
        if ((locator instanceof WithConfigurableTimeout) && (driver instanceof ConfigurableTimeouts)) {
            ((WithConfigurableTimeout) locator).setTimeOutInSeconds((int) getLocatorTimeout());
        }
        return locator;
    }

    private long getLocatorTimeout() {
        if (StepEventBus.getEventBus().aStepInTheCurrentTestHasFailed() || (MethodTiming.forThisThread().isInQuickMethod())) {
            return 0;
        } else {
            return TimeUnit.SECONDS.convert(implicitTimeoutInMilliseconds, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void setImplicitTimeout(Duration implicitTimeout) {
        if (driverIsDisabled()) { return;}

        if (driver instanceof ConfigurableTimeouts) {
            ((ConfigurableTimeouts) driver).setImplicitTimeout(implicitTimeout);
        }
    }

    @Override
    public Duration getCurrentImplicitTimeout() {
        if (driverIsDisabled()) { return Duration.ofSeconds(0); }

        if (driver instanceof ConfigurableTimeouts) {
            return ((ConfigurableTimeouts) driver).getCurrentImplicitTimeout();
        }
        return DefaultTimeouts.DEFAULT_IMPLICIT_WAIT_TIMEOUT;
    }

    @Override
    public Duration resetTimeouts() {
        if (driverIsDisabled()) { return Duration.ofSeconds(0);}
        if (driver instanceof ConfigurableTimeouts) {
            return ((ConfigurableTimeouts) driver).resetTimeouts();
        }
        return DefaultTimeouts.DEFAULT_IMPLICIT_WAIT_TIMEOUT;
    }

    public String getFoundBy() {
        return foundBy;
    }


    /**
     * Check that an element contains a text value
     *
     * @param textValue
     */
    @Override
    public void shouldContainText(String textValue) {
        if (!containsText(textValue)) {
            String errorMessage = String.format(
                    "The text '%s' was not found in the web element. Element text '%s'.", textValue, getElement().getText());
            failWithMessage(errorMessage);
        }
    }

    /**
     * Check that an element exactly matches a text value
     *
     * @param textValue
     */
    @Override
    public void shouldContainOnlyText(String textValue) {
        if (!containsOnlyText(textValue)) {
            String errorMessage = String.format(
                    "The text '%s' does not match the elements text '%s'.", textValue, getElement().getText());
            failWithMessage(errorMessage);
        }
    }

    @Override
    public void shouldContainSelectedOption(String textValue) {
        if (!containsSelectOption(textValue)) {
            String errorMessage = String.format(
                    "The list element '%s' was not found in the web element", textValue);
            failWithMessage(errorMessage);
        }
    }

    /**
     * Check that an element does not contain a text value
     *
     * @param textValue
     */
    @Override
    public void shouldNotContainText(String textValue) {
        if (containsText(textValue)) {
            String errorMessage = String.format(
                    "The text '%s' was found in the web element when it should not have. Element text '%s'.", textValue, getElement().getText());
            failWithMessage(errorMessage);
        }
    }

    @Override
    public void shouldBeEnabled() {
        if (!isEnabled()) {
            String errorMessage = String.format(
                    "Field '%s' should be enabled", toString());
            failWithMessage(errorMessage);
        }
    }

    @Override
    public boolean isEnabled() {
        if (driverIsDisabled()) { return false;}
        return (getElement() != null) && (getElement().isEnabled());
    }

    @Override
    public void shouldNotBeEnabled() {
        if (isEnabled()) {
            String errorMessage = String.format(
                    "Field '%s' should not be enabled", toString());
            failWithMessage(errorMessage);
        }
    }

    /**
     * Type a value into a field, making sure that the field is empty first.
     *
     * @param value
     */
    @Override
    public WebElementFacade type(final String value) {
        logIfVerbose("Type '" + value + "'");

        if (driverIsDisabled()) { return this;}

        waitUntilElementAvailable();
        clear();
        getElement().sendKeys(value);
        notifyScreenChange();
        return this;
    }

    /**
     * Type a value into a field and then press Enter, making sure that the field is empty first.
     *
     * @param value
     */
    @Override
    public WebElementFacade typeAndEnter(final String value) {
        logIfVerbose("Type and enter '" + value + "'");

        if (driverIsDisabled()) { return this;}

        waitUntilElementAvailable();
        clear();
        getElement().sendKeys(value, Keys.ENTER);
        notifyScreenChange();
        return this;
    }

    /**
     * Type a value into a field and then press TAB, making sure that the field is empty first.
     * This currently is not supported by all browsers, notably Firefox.
     *
     * @param value
     */
    @Override
    public WebElementFacade typeAndTab(final String value) {
        logIfVerbose("Type and tab '" + value + "'");

        if (driverIsDisabled()) { return this;}

        waitUntilElementAvailable();
        clear();

        getElement().sendKeys(value);
        getElement().sendKeys(Keys.TAB);

        getClock().pauseFor(100);
        notifyScreenChange();
        return this;
    }

    @Override
    public void setWindowFocus() {
        if (driverIsDisabled()) { return;}

        getJavascriptExecutorFacade().executeScript("window.focus()");
    }

    private DropdownSelector select() {
        return new DropdownSelector(this);
    }

    private DropdownDeselector deselect() {
        return new DropdownDeselector(this);
    }

    @Override
    public WebElementFacade deselectAll() {
        return deselect().all();
    }

    @Override
    public WebElementFacade deselectByIndex(int indexValue) {
        return deselect().byIndex(indexValue);
    }

    @Override
    public WebElementFacade deselectByVisibleText(String label) {
        return deselect().byVisibleText(label);
    }

    @Override
    public WebElementFacade deselectByValue(String value) {
        return deselect().byValue(value);
    }

    @Override
    public WebElementFacade selectByVisibleText(final String label) {
        return select().byVisibleText(label);
    }

    @Override
    public String getSelectedVisibleTextValue() {
        return select().visibleTextValue();
    }

    @Override
    public WebElementFacade selectByValue(String value) {
        return select().byValue(value);
    }

    @Override
    public String getSelectedValue() {
        return select().value();
    }

    @Override
    public WebElementFacade selectByIndex(int indexValue) {
        return select().byIndex(indexValue);
    }

    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        if (driverIsDisabled()) {
            return null;
        }
        return getWrappedElement().getScreenshotAs(target);
    }

    protected void waitUntilElementAvailable() {
        if (driverIsDisabled()) {
            return;
        }
        withTimeoutOf((int)waitForTimeoutInMilliseconds, TimeUnit.MILLISECONDS).waitUntilEnabled();
    }

    protected boolean driverIsDisabled() {
        return StepEventBus.getEventBus().webdriverCallsAreSuspended();
    }

    /**
     * Returns true if an element is present on the screen, whether visible or not.
     */
    public boolean isPresent() {
        if (driverIsDisabled()) {
            return false;
        }

        try {
            WebElement element = getElement();

            if (element == null) {
                return false;
            }
            element.isDisplayed();
            return true;
        } catch (ElementNotVisibleException e) {
            return true;
        } catch (NotFoundException e) {
            return false;
        } catch (ElementNotFoundAfterTimeoutError timeoutError) {
            return false;
        }
    }

    @Override
    public void shouldBePresent() {
        if (!isPresent()) {
            failWithMessage("Field should be present");
        }
    }

    @Override
    public void shouldNotBePresent() {
        if (isPresent()) {
            failWithMessage("Field should not be present");
        }
    }

    private void failWithMessage(String errorMessage) {
        throw new AssertionError(getErrorMessage(errorMessage));
    }

    private void checkPresenceOfWebElement() {
        try {
            if (!driverIsDisabled()) {
                waitForCondition().until(WebElementExpectations.elementIsDisplayed(this));
            }
        } catch (Throwable error) {
            if (webElement != null) {
                throwShouldBeVisibleErrorWithCauseIfPresent(error, error.getMessage());
            } else {
                throwNoSuchElementExceptionWithCauseIfPresent(error, error.getMessage());
            }
        }
    }

    @Override
    public WebElementFacade waitUntilVisible() {
        checkPresenceOfWebElement();
        return this;
    }

    @Override
    public WebElementFacade waitUntilPresent() {
        try {
            if (!driverIsDisabled()) {
                waitForCondition().until(WebElementExpectations.elementIsPresent(this));
            }
        } catch (TimeoutException timeout) {
            throwShouldBePresentErrorWithCauseIfPresent(timeout, timeout.getMessage());
        }
        return this;
    }


    private void throwNoSuchElementExceptionWithCauseIfPresent(final Throwable timeout, final String defaultMessage) {
        String timeoutMessage = (timeout.getCause() != null) ? timeout.getCause().getMessage() : timeout.getMessage();
        String finalMessage = (StringUtils.isNotEmpty(timeoutMessage)) ? timeoutMessage : defaultMessage;
        throw new NoSuchElementException(finalMessage, timeout);
    }

    private void throwShouldBeVisibleErrorWithCauseIfPresent(final Throwable timeout, final String defaultMessage) {
        String timeoutMessage = (timeout.getCause() != null) ? timeout.getCause().getMessage() : timeout.getMessage();
        String finalMessage = (StringUtils.isNotEmpty(timeoutMessage)) ? timeoutMessage : defaultMessage;
        throw new ElementShouldBeVisibleException(finalMessage, timeout);
    }

    private void throwShouldBeInvisibleErrorWithCauseIfPresent(final Throwable timeout, final String defaultMessage) {
        String timeoutMessage = (timeout.getCause() != null) ? timeout.getCause().getMessage() : timeout.getMessage();
        String finalMessage = (StringUtils.isNotEmpty(timeoutMessage)) ? timeoutMessage : defaultMessage;
        throw new ElementShouldBeInvisibleException(finalMessage, timeout);
    }

    private void throwShouldBePresentErrorWithCauseIfPresent(final Throwable timeout, final String defaultMessage) {
        String timeoutMessage = (timeout.getCause() != null) ? timeout.getCause().getMessage() : timeout.getMessage();
        String finalMessage = (StringUtils.isNotEmpty(timeoutMessage)) ? timeoutMessage : defaultMessage;
        throw new ElementShouldBePresentException(finalMessage, timeout);
    }

    private static final List<String> HTML_ELEMENTS_WITH_VALUE_ATTRIBUTE = Arrays.asList("input", "button", "option");

    private boolean hasValueAttribute(WebElement element) {
        String tag = element.getTagName().toLowerCase();
        return HTML_ELEMENTS_WITH_VALUE_ATTRIBUTE.contains(tag);

    }

    @Override
    public Wait<WebDriver> waitForCondition() {
        return new FluentWait<>(driver, webdriverClock, sleeper)
                .withTimeout(waitForTimeoutInMilliseconds, TimeUnit.MILLISECONDS)
                .pollingEvery(WAIT_FOR_ELEMENT_PAUSE_LENGTH, TimeUnit.MILLISECONDS)
                .ignoring(NoSuchElementException.class, NoSuchFrameException.class);
    }

    @Override
    public WebElementFacade waitUntilNotVisible() {
        try {
            if (!driverIsDisabled()) {
                waitForCondition().until(elementIsNotDisplayed(this));
            }
        } catch (TimeoutException timeout) {
            throwShouldBeInvisibleErrorWithCauseIfPresent(timeout, "Expected hidden element was displayed");
        }
        return this;
    }

    @Override
    public String getValue() {
        checkPresenceOfWebElement();
        return getElement().getAttribute("value");
    }

    @Override
    public boolean isSelected() {
        return getElement().isSelected();
    }

    @Override
    public String getText() {
        if (driverIsDisabled()) { return "";}

        checkPresenceOfWebElement();
        return getElement().getText();
    }

    @Override
    public WebElementFacade waitUntilEnabled() {
        try {
            if (!driverIsDisabled()) {
                waitForCondition().until(elementIsEnabled(this));
            }
        } catch (TimeoutException timeout) {
            throw new ElementShouldBeEnabledException("Expected enabled element was not enabled", timeout);
        }
        return this;
    }

    @Override
    public WebElementFacade waitUntilClickable() {
        try {
            if (!driverIsDisabled()) {
                waitForCondition().until(elementIsClickable(this));
            }
        } catch (TimeoutException timeout) {
            throw new ElementShouldBeEnabledException("Expected enabled element was not enabled", timeout);
        }
        return this;
    }

    @Override
    public WebElementFacade waitUntilDisabled() {
        try {
            if (!driverIsDisabled()) {
                waitForCondition().until(elementIsNotEnabled(this));
            }
        } catch (TimeoutException timeout) {
            throw new ElementShouldBeDisabledException("Expected disabled element was not disabled", timeout);
        }
        return this;
    }

    @Override
    public String getTextValue() {
        if (driverIsDisabled()) { return "";}

        waitUntilPresent();

        if (!isVisible()) {
            return "";
        }

        if (valueAttributeSupportedAndDefinedIn(getElement())) {
            return getValue();
        }

        if (!StringUtils.isEmpty(getElement().getText())) {
            return getElement().getText();
        }
        return "";
    }

    @Override
    public WebElementState expect(String errorMessage) {
        return copy().expectingErrorMessage(errorMessage);
    }

    private Optional<String> expectedErrorMessage = Optional.empty();

    protected WebElementState expectingErrorMessage(String errorMessage) {
        this.expectedErrorMessage = Optional.of(errorMessage);
        return this;
    }

    protected String getErrorMessage(String defaultErrorMessage) {
        return expectedErrorMessage.orElse(defaultErrorMessage);
    }

    private boolean valueAttributeSupportedAndDefinedIn(final WebElement element) {
        return hasValueAttribute(element) && StringUtils.isNotEmpty(getValue());
    }

    /**
     * Wait for an element to be visible and enabled, and then click on it.
     */
    @Override
    public void click() {
        if (driverIsDisabled()) { return;}

        waitUntilElementAvailable();
        logClick();
        getElement().click();
        notifyScreenChange();
    }

    private void logClick() {
        logIfVerbose("click");
    }

    private void logIfVerbose(String logMessage) {
        if (useVerboseLogging()) {
            LOGGER.debug(logMessage + " : " +  toString());
        }
    }

    private boolean useVerboseLogging() {
        return ThucydidesSystemProperty.THUCYDIDES_VERBOSE_STEPS.booleanFrom(environmentVariables);
    }

    private boolean isMobileDriver() {
        return AppiumDriver.class.isAssignableFrom(ThucydidesWebDriverSupport.getDriverClass());
    }

    @Override
    public void clear() {

        if (driverIsDisabled()) { return;}

        if (!isMobileDriver()) {
            getElement().sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        }
        getElement().clear();
    }

    protected void notifyScreenChange() {
        StepEventBus.getEventBus().notifyScreenChange();
    }

    @Override
    public String toString() {
        if (foundBy != null) {
            return foundBy;
        }
        if (bySelector != null) {
            return bySelector.toString();
        }
        if (locator != null) {
            return locator.toString();
        }
        return "<webelement>";
    }

    public void submit() {
        if (driverIsDisabled()) { return;}

        getElement().submit();
    }

    public void sendKeys(CharSequence... keysToSend) {
        if (driverIsDisabled()) { return;}

        getElement().sendKeys(keysToSend);
    }

    public String getTagName() {
        if (driverIsDisabled()) { return "";}

        return getElement().getTagName();
    }

    private Optional<WebDriverFacade> webDriverFacade() {
        if (driver instanceof WebElementFacade) {
            return Optional.of((WebDriverFacade) driver);
        } else {
            return Optional.empty();
        }
    }

    public List<WebElement> findElements(By by) {
        if (driverIsDisabled()) { return new ArrayList<>();}

        return getElement().findElements(by);
    }

    public WebElement findElement(By by) {
        if (driverIsDisabled()) { return this;}

        return getElement().findElement(by);
    }

    @Override
    public WebElement findElement(String by, String using) {
        if (driverIsDisabled()) { return this;}

        if (getElement() instanceof MobileElement) {
            return ((MobileElement)getElement()).findElement(by, using);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public List findElements(String by, String using) {
        if (driverIsDisabled()) { return new ArrayList();}

        if (getElement() instanceof MobileElement) {
            return ((MobileElement)getElement()).findElements(by, using);
        }
        throw new UnsupportedOperationException();
    }
    /**
     * Is this element displayed or not? This method avoids the problem of having to parse an
     * element's "style" attribute.
     * This method respects the semantics of the Selenium isDisplayed() method, and will throw an
     * exception if the element is not found. To simply return true or false depending on whether an element
     * is
     *
     * @return Whether or not the element is displayed
     */
    public boolean isDisplayed() {
        if (driverIsDisabled()) { return false;}

        return getElement().isDisplayed();
    }

    public Point getLocation() {
        return getElement().getLocation();
    }

    public Dimension getSize() {
        return getElement().getSize();
    }

    @Override
    public Rectangle getRect() {
        return getElement().getRect();
    }

    public String getCssValue(String propertyName) {
        return getElement().getCssValue(propertyName);
    }

    public WebElement getWrappedElement() {
        return getElement();
    }

    @Override
    public Coordinates getCoordinates() {
        return ((Locatable) getElement()).getCoordinates();
    }

    @Override
    public boolean containsElements(By bySelector) {
        return !findElements(bySelector).isEmpty();
    }

    @Override
    public boolean containsElements(String xpathOrCssSelector) {
        return !thenFindAll(xpathOrCssSelector).isEmpty();
    }

    @Override
    public void shouldContainElements(By bySelector) {
        if (!containsElements(bySelector)) {
            String errorMessage = String.format(
                    "Could not find contained elements %s in %s", bySelector, getElement().toString());
            failWithMessage(errorMessage);
        }
    }

    @Override
    public void shouldContainElements(String xpathOrCssSelector) {
        if (!containsElements(xpathOrCssSelector)) {
            String errorMessage = String.format(
                    "Could not find contained elements %s in %s", xpathOrCssSelector, getElement().toString());
            failWithMessage(errorMessage);
        }
    }

    @Override
    public boolean hasClass(String cssClassName) {
        String cssClassValue = getAttribute("class").toLowerCase();
        List<String> cssClasses = Splitter.on(" ").omitEmptyStrings().trimResults().splitToList(cssClassValue);
        return cssClasses.contains(cssClassName.toLowerCase());
    }

    public WebElementFacade foundBy(String foundBy) {
        this.foundBy = foundBy;
        return this;
    }

}
