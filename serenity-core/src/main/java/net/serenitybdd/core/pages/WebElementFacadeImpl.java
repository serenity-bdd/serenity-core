package net.serenitybdd.core.pages;

import com.google.common.base.Splitter;
import io.appium.java_client.AppiumDriver;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.SystemTimeouts;
import net.serenitybdd.core.time.InternalSystemClock;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.locators.MethodTiming;
import net.thucydides.core.annotations.locators.WithConfigurableTimeout;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.SystemEnvironmentVariables;
import net.thucydides.core.webdriver.ConfigurableTimeouts;
import net.thucydides.core.webdriver.TemporalUnitConverter;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.exceptions.*;
import net.thucydides.core.webdriver.javascript.JavascriptExecutorFacade;
import net.thucydides.core.webdriver.stubs.WebElementFacadeStub;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.serenitybdd.core.pages.ParameterisedLocator.withArguments;
import static net.serenitybdd.core.pages.WebElementExpectations.*;
import static net.serenitybdd.core.selectors.Selectors.isXPath;
import static net.thucydides.core.ThucydidesSystemProperty.LEGACY_WAIT_FOR_TEXT;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;


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

    public WebElementFacadeImpl(final WebDriver driver, final ElementLocator locator, final WebElement webElement, final long implicitTimeoutInMilliseconds, final long waitForTimeoutInMilliseconds, final By bySelector) {
        this.webElement = webElement;
        this.driver = driver;
        this.locator = locator;
        this.bySelector = bySelector;
        this.webdriverClock = Clock.systemDefaultZone();
        this.sleeper = Sleeper.SYSTEM_SLEEPER;
        this.javascriptExecutorFacade = new JavascriptExecutorFacade(driver);
        this.environmentVariables = Injectors.getInjector().getProvider(EnvironmentVariables.class).get();
        this.implicitTimeoutInMilliseconds = implicitTimeoutInMilliseconds;
        this.waitForTimeoutInMilliseconds = (waitForTimeoutInMilliseconds >= 0) ? waitForTimeoutInMilliseconds : defaultWaitForTimeout();
    }

    public WebElementFacadeImpl(final WebDriver driver, final WebElement webElement, final long implicitTimeoutInMilliseconds, final long waitForTimeoutInMilliseconds, final By bySelector) {
        this.webElement = webElement;
        this.driver = driver;
        this.locator = null;
        this.bySelector = bySelector;
        this.webdriverClock = Clock.systemDefaultZone();
        this.sleeper = Sleeper.SYSTEM_SLEEPER;
        this.javascriptExecutorFacade = new JavascriptExecutorFacade(driver);
        this.environmentVariables = Injectors.getInjector().getProvider(EnvironmentVariables.class).get();
        this.implicitTimeoutInMilliseconds = implicitTimeoutInMilliseconds;
        this.waitForTimeoutInMilliseconds = (waitForTimeoutInMilliseconds >= 0) ? waitForTimeoutInMilliseconds : defaultWaitForTimeout();
    }

    public WebElementFacadeImpl(final WebDriver driver, final ElementLocator locator, final WebElement webElement, final long implicitTimeoutInMilliseconds, final long waitForTimeoutInMilliseconds) {
        this.webElement = webElement;
        this.driver = driver;
        this.locator = locator;
        this.bySelector = null;
        this.webdriverClock = Clock.systemDefaultZone();
        this.sleeper = Sleeper.SYSTEM_SLEEPER;
        this.javascriptExecutorFacade = new JavascriptExecutorFacade(driver);
        this.environmentVariables = Injectors.getInjector().getProvider(EnvironmentVariables.class).get();
        this.implicitTimeoutInMilliseconds = implicitTimeoutInMilliseconds;
        this.waitForTimeoutInMilliseconds = (waitForTimeoutInMilliseconds >= 0) ? waitForTimeoutInMilliseconds : defaultWaitForTimeout();
    }

    public WebElementFacadeImpl(final WebDriver driver, final ElementLocator locator, final WebElement webElement, final long implicitTimeoutInMilliseconds) {
        this(driver, locator, webElement, implicitTimeoutInMilliseconds, implicitTimeoutInMilliseconds);
    }

    public WebElementFacadeImpl(WebDriver driver, ElementLocator locator, WebElement webElement, WebElement resolvedELement, By bySelector, long timeoutInMilliseconds, long waitForTimeoutInMilliseconds) {
        this.webElement = webElement;
        this.resolvedELement = resolvedELement;
        this.driver = driver;
        this.locator = locator;
        this.bySelector = bySelector;
        this.webdriverClock = Clock.systemDefaultZone();
        this.sleeper = Sleeper.SYSTEM_SLEEPER;
        this.javascriptExecutorFacade = new JavascriptExecutorFacade(driver);
        this.environmentVariables = Injectors.getInjector().getProvider(EnvironmentVariables.class).get();
        this.implicitTimeoutInMilliseconds = timeoutInMilliseconds;
        this.waitForTimeoutInMilliseconds = (waitForTimeoutInMilliseconds >= 0) ? waitForTimeoutInMilliseconds : defaultWaitForTimeout();

    }


    private long defaultWaitForTimeout() {
        return ThucydidesSystemProperty.WEBDRIVER_WAIT_FOR_TIMEOUT.integerFrom(environmentVariables, (int) DefaultTimeouts.DEFAULT_WAIT_FOR_TIMEOUT.toMillis());
    }

    private WebElementFacadeImpl copy() {
        return BuildWebElementFacade.from(driver, locator, webElement, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds);
    }

    public WebElementFacadeImpl(final WebDriver driver, final ElementLocator locator, final long implicitTimeoutInMilliseconds) {
        this(driver, locator, null, implicitTimeoutInMilliseconds, implicitTimeoutInMilliseconds);
    }


    public WebElementFacadeImpl(final WebDriver driver, final ElementLocator locator, final long implicitTimeoutInMilliseconds, final long waitForTimeoutInMilliseconds) {
        this(driver, locator, null, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds);
    }

    public static <T extends WebElementFacade> T wrapWebElement(final WebDriver driver, final WebElement element, final long timeoutInMilliseconds, final long waitForTimeoutInMilliseconds) {
        return BuildWebElementFacade.from(driver, element, timeoutInMilliseconds, waitForTimeoutInMilliseconds);
    }

    public static <T extends WebElementFacade> T wrapWebElement(final WebDriver driver, final WebElement element, final long timeoutInMilliseconds, final long waitForTimeoutInMilliseconds, final String foundBy) {
        return BuildWebElementFacade.from(driver, element, timeoutInMilliseconds, waitForTimeoutInMilliseconds, foundBy);
    }

    public static <T extends WebElementFacade> T wrapWebElement(WebDriver driver, WebElement resolvedELement, WebElement element, By bySelector, ElementLocator locator, long timeoutInMilliseconds, long waitForTimeoutInMilliseconds, String foundBy) {
        return BuildWebElementFacade.from(driver, resolvedELement, element, bySelector, locator, timeoutInMilliseconds, waitForTimeoutInMilliseconds, foundBy);
    }

    public static <T extends WebElementFacade> T wrapWebElement(final WebDriver driver, final By bySelector, final long timeoutInMilliseconds, final long waitForTimeoutInMilliseconds, final String foundBy) {
        return BuildWebElementFacade.from(driver, bySelector, timeoutInMilliseconds, waitForTimeoutInMilliseconds, foundBy);
    }

    public static WebElementFacade wrapWebElement(final WebDriver driver, final WebElement element, final long timeout) {
        return BuildWebElementFacade.from(driver, element, timeout);
    }

    public static WebElementFacade wrapWebElement(final WebDriver driver, final WebElement element) {
        return wrapWebElement(driver, element, SystemTimeouts.forTheCurrentTest().getImplicitTimeout());
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

    public WebElement getElement() {
        if (driverIsDisabled()) {
            return new WebElementFacadeStub();
        }
        return getResolvedElement();
    }

    private WebElement getResolvedElement() {
        if (resolvedELement == null) {
            resolvedELement = getElementResolver().resolveForDriver(driver);
        }

        return resolvedELement;

    }

    protected JavascriptExecutorFacade getJavascriptExecutorFacade() {
        return javascriptExecutorFacade;
    }

    protected InternalSystemClock getClock() {
        return clock;
    }


    @Override
    public WebElementFacade then(String xpathOrCssSelector, Object... arguments) {
        return findBy(xpathOrCssSelector, arguments);
    }

    @Override
    public WebElementFacade thenFind(String xpathOrCssSelector, Object... arguments) {
        return findBy(xpathOrCssSelector, arguments);
    }

    @Override
    public WebElementFacade thenFind(String xpathOrCssSelector) {
        return findBy(xpathOrCssSelector);
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
            nestedElement = getElement().findElement((By.xpath(xpathOrCssSelector)));
        } else {
            nestedElement = getElement().findElement((By.cssSelector(xpathOrCssSelector)));
        }

        return wrapWebElement(driver, nestedElement, timeoutInMilliseconds(), waitForTimeoutInMilliseconds, "element located by " + xpathOrCssSelector);
    }

    public <T extends net.serenitybdd.core.pages.WebElementFacade> T findBy(String xpathOrCssSelector, Object... arguments) {
        return findBy(withArguments(xpathOrCssSelector, arguments));
    }


    public long timeoutInMilliseconds() {
        if (driver instanceof WebDriverFacade) {
            return ((WebDriverFacade) driver).getCurrentImplicitTimeout().toMillis();
        } else {
            return implicitTimeoutInMilliseconds;
        }

    }

    @Override
    public ListOfWebElementFacades thenFindAll(String xpathOrCssSelector) {
        logIfVerbose("findAll " + xpathOrCssSelector);
        if (driverIsDisabled()) {
            return new ListOfWebElementFacades(new ArrayList<>());
        }

        List<WebElement> nestedElements;
        if (isXPath(xpathOrCssSelector)) {
            nestedElements = findElements((By.xpath(xpathOrCssSelector)));
        } else {
            nestedElements = findElements((By.cssSelector(xpathOrCssSelector)));
        }

        return webElementFacadesFrom(nestedElements);
    }

    public ListOfWebElementFacades thenFindAll(String xpathOrCssSelector, Object... arguments) {
        return thenFindAll(withArguments(xpathOrCssSelector, arguments));
    }

    private ListOfWebElementFacades webElementFacadesFrom(List<WebElement> nestedElements) {
        List<WebElementFacade> results = new ArrayList<>();
        for (WebElement element : nestedElements) {
            results.add(wrapWebElement(driver, element, timeoutInMilliseconds(), waitForTimeoutInMilliseconds, element.toString()));
        }
        return new ListOfWebElementFacades(results);
    }

    @Override
    public WebElementFacade findBy(By selector) {
        logIfVerbose("findBy " + selector);

        if (driverIsDisabled()) {
            return this;
        }

        WebElement nestedElement = getElement().findElement(selector);
        return wrapWebElement(driver, nestedElement, timeoutInMilliseconds(), waitForTimeoutInMilliseconds, "element located by " + selector.toString());
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
        return getElement().getAttribute(name);
    }

    @Override
    public ListOfWebElementFacades thenFindAll(By... selectors) {
        logIfVerbose("findAll " + selectors);
        if (driverIsDisabled()) {
            return new ListOfWebElementFacades(new ArrayList<>());
        }

        List<WebElement> nestedElements = new ArrayList<>();
        for (By selector : selectors) {
            nestedElements = findElements(selector);
            if (!nestedElements.isEmpty()) {
                break;
            }
        }
        return webElementFacadesFrom(nestedElements);
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
        return withTimeoutOf(timeout, TemporalUnitConverter.fromTimeUnit(unit));
    }

    public WebElementFacade withTimeoutOf(int timeout, TemporalUnit unit) {
        return withTimeoutOf(Duration.of(timeout, unit));
    }

    public WebElementFacade withTimeoutOf(Duration duration) {
        return wrapWebElement(driver, resolvedELement, webElement, bySelector, locator, duration.toMillis(), duration.toMillis(), foundBy);
    }

    /**
     * Is this web element present and visible on the screen
     * This method will not throw an exception if the element is not on the screen at all.
     * If the element is not visible, the method will wait a bit to see if it appears later on.
     */
    @Override
    public boolean isVisible() {

        if (driverIsDisabled()) {
            return false;
        }

        try {
            WebElement element = getElement();

            if (element == null) {
                return false;
            }

            if (shouldWaitForResult()) {
                return waitForCondition().until(ExpectedConditions.visibilityOf(element)).isDisplayed();
            } else {
                return element.isDisplayed();
            }

        } catch (ElementNotInteractableException | NoSuchElementException | StaleElementReferenceException | TimeoutException e) {
            return false;
        }
    }

    private boolean shouldWaitForResult() {
        return !MethodTiming.forThisThread().isInQuickMethod();
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
        if (driverIsDisabled()) {
            return false;
        }

        try {
            return (getElement() != null) && getElement().isEnabled();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Checks whether a web element is visible.
     * Throws an AssertionError if the element is not rendered.
     */
    @Override
    public WebElementState shouldBeVisible() {
        if (!isVisible()) {
            failWithMessage("Element should be visible");
        }
        return this;
    }

    /**
     * Checks whether a web element is visible.
     * Throws an AssertionError if the element is not rendered.
     */
    @Override
    public WebElementState shouldBeCurrentlyVisible() {
        if (!isCurrentlyVisible()) {
            failWithMessage("Element should be visible");
        }
        return this;
    }

    /**
     * Checks whether a web element is not visible.
     * Throws an AssertionError if the element is not rendered.
     */
    @Override
    public WebElementState shouldNotBeVisible() {
        if (isCurrentlyVisible()) {
            failWithMessage("Element should not be visible");
        }
        return this;
    }

    /**
     * Checks whether a web element is not visible straight away.
     * Throws an AssertionError if the element is not rendered.
     */
    @Override
    public WebElementState shouldNotBeCurrentlyVisible() {
        if (isCurrentlyVisible()) {
            failWithMessage("Element should not be visible");
        }
        return this;
    }

    /**
     * Does this element currently have the focus.
     */
    @Override
    public boolean hasFocus() {
        if (driverIsDisabled()) {
            return false;
        }

        JavascriptExecutorFacade js = new JavascriptExecutorFacade(driver);
        WebElement activeElement = (WebElement) js.executeScript("return window.document.activeElement");
        return getElement().equals(activeElement);
    }

    /**
     * Does this element contain a given text?
     */
    @Override
    public boolean containsText(final String value) {
        if (driverIsDisabled()) {
            return false;
        }

        WebElement element = getElement();
        return ((element != null) && (element.getText().contains(value)));
    }

    @Override
    public boolean containsValue(String value) {
        if (driverIsDisabled()) {
            return false;
        }

        WebElement element = getElement();
        return ((element != null) && (element.getAttribute("value").contains(value)));
    }


    /**
     * Does this element exactly match  given text?
     */
    @Override
    public boolean containsOnlyText(final String value) {
        if (driverIsDisabled()) {
            return false;
        }

        WebElement element = getElement();

        if (element == null) return false;

        String text = element.getText();
        if (text.isEmpty()) {
            // https://github.com/serenity-bdd/serenity-core/issues/2134
            // maybe it's an input element?
            text = element.getAttribute("value");
        }
        return value.equals(text);
    }

    /**
     * Does this dropdown contain the specified value.
     */
    @Override
    public boolean containsSelectOption(final String value) {
        if (driverIsDisabled()) {
            return false;
        }

        return getSelectOptions().contains(value);
    }

    @Override
    public List<String> getSelectOptions() {
        if (driverIsDisabled()) {
            return new ArrayList<>();
        }

        if (getElement() != null) {
            return findElements(By.tagName("option")).stream().map(WebElement::getText).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> getSelectOptionValues() {
        if (driverIsDisabled()) {
            return new ArrayList<>();
        }

        if (getElement() != null) {
            return findElements(By.tagName("option")).stream().map(elt -> elt.getAttribute("value")).collect(Collectors.toList());
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
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended() || (MethodTiming.forThisThread().isInQuickMethod())) {
            return 0;
        } else {
            return TimeUnit.SECONDS.convert(implicitTimeoutInMilliseconds, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void setImplicitTimeout(Duration implicitTimeout) {
        if (driverIsDisabled()) {
            return;
        }

        if (driver instanceof ConfigurableTimeouts) {
            ((ConfigurableTimeouts) driver).setImplicitTimeout(implicitTimeout);
        }
    }

    @Override
    public Duration getCurrentImplicitTimeout() {
        if (driverIsDisabled()) {
            return Duration.ofSeconds(0);
        }

        if (driver instanceof ConfigurableTimeouts) {
            return ((ConfigurableTimeouts) driver).getCurrentImplicitTimeout();
        }
        return DefaultTimeouts.DEFAULT_IMPLICIT_WAIT_TIMEOUT;
    }

    @Override
    public Duration resetTimeouts() {
        if (driverIsDisabled()) {
            return Duration.ofSeconds(0);
        }
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
    public WebElementState shouldContainText(String textValue) {
        if (!containsText(textValue)) {
            String errorMessage = String.format("The text '%s' was not found in the web element. Element text '%s'.", textValue, getElement().getText());
            failWithMessage(errorMessage);
        }
        return this;
    }

    /**
     * Check that an element exactly matches a text value
     *
     * @param textValue
     */
    @Override
    public WebElementState shouldContainOnlyText(String textValue) {
        if (!containsOnlyText(textValue)) {
            String errorMessage = String.format("The text '%s' does not match the elements text '%s'.", textValue, getElement().getText());
            failWithMessage(errorMessage);
        }
        return this;
    }

    @Override
    public WebElementState shouldContainSelectedOption(String textValue) {
        if (!containsSelectOption(textValue)) {
            String errorMessage = String.format("The list element '%s' was not found in the web element", textValue);
            failWithMessage(errorMessage);
        }
        return this;
    }

    /**
     * Check that an element does not contain a text value
     *
     * @param textValue
     */
    @Override
    public WebElementState shouldNotContainText(String textValue) {
        if (containsText(textValue)) {
            String errorMessage = String.format("The text '%s' was found in the web element when it should not have. Element text '%s'.", textValue, getElement().getText());
            failWithMessage(errorMessage);
        }
        return this;
    }

    @Override
    public WebElementState shouldBeEnabled() {
        if (!isCurrentlyEnabled()) {
            String errorMessage = String.format("Field '%s' should be enabled", toString());
            failWithMessage(errorMessage);
        }
        return this;
    }

    @Override
    public boolean isEnabled() {
        if (driverIsDisabled()) {
            return false;
        }
        if (getElement() == null) {
            return false;
        }

        if (shouldWaitForResult()) {
            try {
                waitForCondition().until(webDriver -> getElement().isEnabled());
            } catch (TimeoutException timeout) {
                return false;
            }
        }

        return getElement().isEnabled();
    }


    @Override
    public WebElementState shouldNotBeEnabled() {

        if (isCurrentlyEnabled()) {
            String errorMessage = String.format("Field '%s' should not be enabled", toString());
            failWithMessage(errorMessage);
        }
        return this;
    }

    /**
     * Check to see if the element is clickable
     */
    public boolean isClickable() {
        try {
            if (!driverIsDisabled() && shouldWaitForResult() && getElement() != null) {
                waitForCondition().until(elementToBeClickable(getElement()));
                return true;
            }
        } catch (TimeoutException timeout) {
            return false;
        }
        return false;
    }

    /**
     * Type a value into a field, making sure that the field is empty first.
     *
     * @param keysToSend
     */
    @Override
    public WebElementFacade type(CharSequence... keysToSend) {
        keysToSend = nonNullCharSequenceFrom(keysToSend);
        logIfVerbose("Type '" + keysToSend + "'");

        if (driverIsDisabled()) {
            return this;
        }

        clear();
        getElement().sendKeys(keysToSend);
        notifyScreenChange();
        return this;
    }

    private CharSequence[] nonNullCharSequenceFrom(CharSequence... charSequences) {
        return Arrays.stream(charSequences).filter(chars -> chars != null).collect(Collectors.toList()).toArray(new CharSequence[]{});
    }
    /**
     * Type a value into a field and then press Enter, making sure that the field is empty first.
     *
     * @param value
     */
    @Override
    public WebElementFacade typeAndEnter(final String value) {
        logIfVerbose("Type and enter '" + value + "'");

        if (driverIsDisabled()) {
            return this;
        }

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

        if (driverIsDisabled()) {
            return this;
        }

        clear();

        getElement().sendKeys(value);
        getElement().sendKeys(Keys.TAB);

        getClock().pauseFor(100);
        notifyScreenChange();
        return this;
    }

    @Override
    public void setWindowFocus() {
        if (driverIsDisabled()) {
            return;
        }

        getJavascriptExecutorFacade().executeScript("window.focus()");
    }


    @Override
    public FluentDropdownSelect select() {
        return new FluentDropdownSelect(this);
    }

    @Override
    public FluentDropdownDeselect deselect() {
        return new FluentDropdownDeselect(this);
    }

    private DropdownSelector dropdownSelect() {
        return new DropdownSelector(this);
    }

    private DropdownDeselector dropdownDeselect() {
        return new DropdownDeselector(this);
    }

    @Override
    public WebElementFacade deselectAll() {
        return dropdownDeselect().all();
    }

    @Override
    public WebElementFacade deselectByIndex(int indexValue) {
        return dropdownDeselect().byIndex(indexValue);
    }

    @Override
    public WebElementFacade deselectByVisibleText(String label) {
        return dropdownDeselect().byVisibleText(label);
    }

    @Override
    public WebElementFacade deselectByValue(String value) {
        return dropdownDeselect().byValue(value);
    }

    @Override
    public WebElementFacade selectByVisibleText(final String label) {
        return dropdownSelect().byVisibleText(label);
    }

    @Override
    public String getSelectedVisibleTextValue() {
        return dropdownSelect().visibleTextValue();
    }

    @Override
    public String getFirstSelectedOptionVisibleText() {
        return new Select(this).getFirstSelectedOption().getText();
    }

    @Override
    public List<String> getSelectedVisibleTexts() {
        return dropdownSelect().visibleTextValues();
    }

    @Override
    public String getFirstSelectedOptionValue() {
        return new Select(this).getFirstSelectedOption().getAttribute("value");
    }

    @Override
    @Deprecated
    public WebElementFacade selectByValue(String value) {
        return dropdownSelect().byValue(value);
    }

    @Override
    public String getSelectedValue() {
        return dropdownSelect().value();
    }

    @Override
    public List<String> getSelectedValues() {
        return dropdownSelect().values();
    }

    @Override
    @Deprecated
    public WebElementFacade selectByIndex(int indexValue) {
        return dropdownSelect().byIndex(indexValue);
    }

    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        if (driverIsDisabled()) {
            return null;
        }
        return getElement().getScreenshotAs(target);
    }

    protected void waitUntilElementAvailable() {
        if (driverIsDisabled()) {
            return;
        }
        withTimeoutOf((int) waitForTimeoutInMilliseconds, TimeUnit.MILLISECONDS).waitUntilEnabled();
    }

    protected void waitUntilElementPresent() {
        if (driverIsDisabled()) {
            return;
        }
        withTimeoutOf((int) waitForTimeoutInMilliseconds, TimeUnit.MILLISECONDS).waitUntilPresent();
    }

    protected boolean driverIsDisabled() {
        return StepEventBus.getEventBus().webdriverCallsAreSuspended();
    }

    private boolean elementIsPresent() {
        try {
            WebElement element = getElement();

            if (getElement() == null) {
                return false;
            }
            element.isDisplayed();
            return true;
        } catch (ElementNotInteractableException e) {
            return true;
        } catch (NotFoundException | ElementNotFoundAfterTimeoutError e) {
            return false;
        }
    }

    /**
     * Returns true if an element is present on the screen, whether visible or not.
     */
    public boolean isPresent() {
        if (driverIsDisabled()) {
            return false;
        }
        return elementIsPresent();
    }


    @Override
    public WebElementState shouldBePresent() {
        if (!isPresent()) {
            failWithMessage("Field should be present");
        }
        return this;
    }

    @Override
    public WebElementState shouldNotBePresent() {
        if (isPresent()) {
            failWithMessage("Field should not be present");
        }
        return this;
    }

    @Override
    public WebElementState shouldBeSelected() {
        if (!isSelected()) {
            failWithMessage("Field should be selected");
        }
        return this;
    }

    @Override
    public WebElementState shouldNotBeSelected() {
        if (isSelected()) {
            failWithMessage("Field should not be selected");
        }
        return this;
    }

    private void failWithMessage(String errorMessage) {
        throw new AssertionError(getErrorMessage(errorMessage));
    }

    private void checkPresenceOfWebElement() {
        try {
            if (!driverIsDisabled() && shouldWaitForResult()) {
                waitForCondition().until(elementIsDisplayed(this));
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

    private boolean hasValueAttribute(WebElement element) {
        try {
            return element.getAttribute("value") != null;
        } catch (UnsupportedCommandException exception){
            return false;
        }
    }

    @Override
    public Wait<WebDriver> waitForCondition() {
        return new FluentWait<>(driver, webdriverClock, sleeper).withTimeout(Duration.ofMillis(waitForTimeoutInMilliseconds)).pollingEvery(Duration.ofMillis(WAIT_FOR_ELEMENT_PAUSE_LENGTH)).ignoring(NoSuchElementException.class, NoSuchFrameException.class);
    }

    @Override
    public WebElementFacade waitUntilNotVisible() {
        if (driverIsDisabled()) {
            return this;
        }

        if (!isCurrentlyVisible()) {
            return this;
        }

        try {
            waitForCondition().until(elementIsNotDisplayed(this));
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
        if (driverIsDisabled()) {
            return "";
        }

        if (LEGACY_WAIT_FOR_TEXT.booleanFrom(environmentVariables, false)) {
            checkPresenceOfWebElement();
        }
        return getElement().getText();
    }

    @Override
    public String getTextContent() {
        if (driverIsDisabled()) {
            return "";
        }

        return getElement().getAttribute("textContent");
    }

    @Override
    public boolean isDisabled() {
        if (driverIsDisabled()) {
            return true;
        }
        return !getElement().isEnabled();
    }

    private boolean driverIsActive() {
        return ((driver != null) && (!driverIsDisabled()));
    }

    @Override
    public WebElementFacade waitUntilEnabled() {
        try {
            if (driverIsActive()) {
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
            if (driverIsActive()) {
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
            if (driverIsActive()) {
                waitForCondition().until(elementIsNotEnabled(this));
            }
        } catch (TimeoutException timeout) {
            throw new ElementShouldBeDisabledException("Expected disabled element was not disabled", timeout);
        }
        return this;
    }

    @Override
    public String getTextValue() {
        if (driverIsDisabled()) {
            return "";
        }

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
     * Click on an element, with or without waiting for it to be visible and enabled
     */
    @Override
    public void click(ClickStrategy clickStrategy) {
        if (driverIsDisabled()) {
            return;
        }

        switch (clickStrategy) {
            case WAIT_UNTIL_ENABLED:
                waitUntilElementAvailable();
                break;
            case WAIT_UNTIL_PRESENT:
                waitUntilElementPresent();
                break;
            case IMMEDIATE:
                break;
        }

        logClick();
        getElement().click();
        notifyScreenChange();
    }

    /**
     * Wait for an element to be visible and enabled, and then click on it.
     */
    @Override
    public void click() {
        click(ClickStrategy.WAIT_UNTIL_ENABLED);
    }

    public void doubleClick() {
        Actions actions = new Actions(driver);
        actions.doubleClick(this);
    }

    public void contextClick() {
        Actions actions = new Actions(driver);
        actions.contextClick(this);
    }

    private void logClick() {
        logIfVerbose("click");
    }

    private void logIfVerbose(String logMessage) {
        if (useVerboseLogging()) {
            LOGGER.debug(logMessage + " : " + toString());
        }
    }

    private boolean useVerboseLogging() {
        return ThucydidesSystemProperty.THUCYDIDES_VERBOSE_STEPS.booleanFrom(environmentVariables);
    }

    @Override
    public void clear() {

        if (driverIsDisabled()) {
            return;
        }
        getElement().clear();
    }

    protected void notifyScreenChange() {
        StepEventBus.getEventBus().notifyScreenChange();
    }

    @Override
    public String toString() {

        if (webElement != null && !driverIsDisabled()) {
            return webElement.toString();
        }
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
        getElement().submit();
    }

    public void sendKeys(CharSequence... keysToSend) {
        getElement().sendKeys(nonNullCharSequenceFrom(keysToSend));
    }

    public String getTagName() {
        return getElement().getTagName();
    }

    public List<WebElement> findElements(By by) {
        return getElement().findElements(by);
    }

    public WebElement findElement(By by) {
        return getElement().findElement(by);
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

    @Override
    public boolean containsElements(By bySelector) {
        return !findElements(bySelector).isEmpty();
    }

    @Override
    public boolean containsElements(String xpathOrCssSelector) {
        return !thenFindAll(xpathOrCssSelector).isEmpty();
    }

    @Override
    public WebElementState shouldContainElements(By bySelector) {
        if (!containsElements(bySelector)) {
            String errorMessage = String.format("Could not find contained elements %s in %s", bySelector, getElement().toString());
            failWithMessage(errorMessage);
        }
        return this;
    }

    @Override
    public WebElementState shouldContainElements(String xpathOrCssSelector) {
        if (!containsElements(xpathOrCssSelector)) {
            String errorMessage = String.format("Could not find contained elements %s in %s", xpathOrCssSelector, getElement().toString());
            failWithMessage(errorMessage);
        }
        return this;
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

    @Override
    public Coordinates getCoordinates() {
        return ((Locatable) getElement()).getCoordinates();
    }

    @Override
    public WebElement getWrappedElement() {
        return getResolvedElement();
    }

    @Override
    public String getDomProperty(String name) {
        return getResolvedElement().getDomProperty(name);
    }

    @Override
    public String getDomAttribute(String name) {
        return getResolvedElement().getDomAttribute(name);
    }

    @Override
    public String getAriaRole() {
        return getResolvedElement().getAriaRole();
    }

    @Override
    public String getAccessibleName() {
        return getResolvedElement().getAccessibleName();
    }

    @Override
    public SearchContext getShadowRoot() {
        return getResolvedElement().getShadowRoot();
    }

    @Override
    public ListOfWebElementFacades findNestedElementsMatching(ResolvableElement nestedElement) {
        return nestedElement.resolveAllFor(this);
    }
    public static ListOfWebElementFacades fromWebElements(List<WebElement> elements) {
        List<WebElementFacade> facades = elements.stream().map(element -> WebElementFacadeImpl.wrapWebElement(Serenity.getDriver(), element)).collect(Collectors.toList());
        return new ListOfWebElementFacades(facades);
    }
}
