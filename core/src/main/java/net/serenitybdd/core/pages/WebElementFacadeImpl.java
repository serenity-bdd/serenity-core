package net.serenitybdd.core.pages;

import ch.lambdaj.function.convert.Converter;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.FindsByAccessibilityId;
import io.appium.java_client.FindsByAndroidUIAutomator;
import io.appium.java_client.FindsByIosUIAutomation;
import net.serenitybdd.core.time.InternalSystemClock;
import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.locators.MethodTiming;
import net.thucydides.core.annotations.locators.WithConfigurableTimeout;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.pages.jquery.JQueryEnabledPage;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.ConfigurableTimeouts;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.exceptions.*;
import net.thucydides.core.webdriver.javascript.JavascriptExecutorFacade;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static ch.lambdaj.Lambda.convert;
import static net.serenitybdd.core.pages.Selectors.isXPath;


/**
 * A proxy class for a web element, providing some more methods.
 */
public class WebElementFacadeImpl implements WebElementFacade, net.thucydides.core.pages.WebElementFacade {

    private final WebElement webElement;
    private final WebDriver driver;
    private final long implicitTimeoutInMilliseconds;
    private final long waitForTimeoutInMilliseconds;
    private static final int WAIT_FOR_ELEMENT_PAUSE_LENGTH = 250;
    private final Sleeper sleeper;
    private final Clock webdriverClock;
    private JavascriptExecutorFacade javascriptExecutorFacade;
    private InternalSystemClock clock = new InternalSystemClock();
    private final EnvironmentVariables environmentVariables;

    private ElementLocator locator;
    private WebElement resolvedELement;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebElementFacadeImpl.class);

    public WebElementFacadeImpl(final WebDriver driver,
                                   final ElementLocator locator,
                                   final WebElement webElement,
                                   final long implicitTimeoutInMilliseconds,
                                   final long waitForTimeoutInMilliseconds) {
        this.webElement = webElement;
        this.driver = driver;
        this.locator = locator;
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


    private long defaultWaitForTimeout() {
        return ThucydidesSystemProperty.WEBDRIVER_WAIT_FOR_TIMEOUT.integerFrom(environmentVariables,
                (int) DefaultTimeouts.DEFAULT_WAIT_FOR_TIMEOUT.in(TimeUnit.MILLISECONDS));
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
        return (T) new WebElementFacadeImpl(driver, null, element, timeoutInMilliseconds, waitForTimeoutInMilliseconds);

    }

    public static <T extends WebElementFacade> T wrapWebElement(final WebDriver driver,
                                                                final WebElement element,
                                                                final long timeout) {
        return (T) new WebElementFacadeImpl(driver, null, element, timeout, timeout);

    }

    protected WebElement getElement() {
        WebElement result;

        try {
            if (resolvedElement() != null) {
                result = resolvedElement();
            } else if (locator == null) {
                result = null;
            } else {
                resolvedELement = getLocator().findElement();
                if (resolvedELement == null) {
                    throw new ElementNotVisibleException(locator.toString());
                }
                result = resolvedELement;
            }
        } catch (Throwable t) {
            throw t;
        }
        return result;
    }

    private WebElement resolvedElement() {
        return (webElement != null) ? webElement : resolvedELement;
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
        if (isXPath(xpathOrCssSelector)) {
            nestedElement = getElement().findElement((By
                    .xpath(xpathOrCssSelector)));
        } else {
            nestedElement = getElement().findElement((By
                    .cssSelector(xpathOrCssSelector)));
        }

        return wrapWebElement(driver, nestedElement, timeoutInMilliseconds(), waitForTimeoutInMilliseconds);
    }


    public long timeoutInMilliseconds() {
        if (driver instanceof WebDriverFacade) {
            return ((WebDriverFacade) driver).getCurrentImplicitTimeout().in(TimeUnit.MILLISECONDS);
        } else {
            return implicitTimeoutInMilliseconds;
        }

    }

    @Override
    public List<WebElementFacade> thenFindAll(String xpathOrCssSelector) {
        logIfVerbose("findAll " + xpathOrCssSelector);
        List<WebElement> nestedElements;
        if (isXPath(xpathOrCssSelector)) {
            nestedElements = findElements((By.xpath(xpathOrCssSelector)));
        } else {
            nestedElements = findElements((By.cssSelector(xpathOrCssSelector)));
        }

        return webElementFacadesFrom(nestedElements);
    }

    private List<WebElementFacade> webElementFacadesFrom(List<WebElement> nestedElements) {
        List<WebElementFacade> results = Lists.newArrayList();
        for (WebElement element : nestedElements) {
            results.add(wrapWebElement(driver, element, timeoutInMilliseconds(), waitForTimeoutInMilliseconds));
        }
        return results;
    }

    @Override
    public WebElementFacade findBy(By selector) {
        logIfVerbose("findBy " + selector);
        WebElement nestedElement = getElement().findElement(selector);
        return wrapWebElement(driver, nestedElement, timeoutInMilliseconds(), waitForTimeoutInMilliseconds);
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
    public List<WebElementFacade> thenFindAll(By selector) {
        logIfVerbose("findAll " + selector);
        List<WebElement> nestedElements = findElements(selector);
        return webElementFacadesFrom(nestedElements);
    }

    @Override
    public WebElement findElementByAccessibilityId(String id) {
        return ((FindsByAccessibilityId) getElement()).findElementByAccessibilityId(id);
    }

    @Override
    public List<WebElement> findElementsByAccessibilityId(String id) {
        return ((FindsByAccessibilityId) getElement()).findElementsByAccessibilityId(id);
    }

    @Override
    public WebElement findElementByAndroidUIAutomator(String using) {
        return ((FindsByAndroidUIAutomator) getElement()).findElementByAndroidUIAutomator(using);
    }

    @Override
    public List<WebElement> findElementsByAndroidUIAutomator(String using) {
        return ((FindsByAndroidUIAutomator) getElement()).findElementsByAndroidUIAutomator(using);
    }

    @Override
    public WebElement findElementByIosUIAutomation(String using) {
        return ((FindsByIosUIAutomation) getElement()).findElementByIosUIAutomation(using);
    }

    @Override
    public List<WebElement> findElementsByIosUIAutomation(String using) {
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
        return wrapWebElement(driver, getElement(), implicitTimeoutInMilliseconds, TimeUnit.MILLISECONDS.convert(timeout, unit));
    }

    /**
     * Is this web element present and visible on the screen
     * This method will not throw an exception if the element is not on the screen at all.
     * If the element is not visible, the method will wait a bit to see if it appears later on.
     */
    @Override
    public boolean isVisible() {

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
        JavascriptExecutorFacade js = new JavascriptExecutorFacade(driver);
        WebElement activeElement = (WebElement) js.executeScript("return window.document.activeElement");
        return getElement().equals(activeElement);
    }

    /**
     * Does this element contain a given text?
     */
    @Override
    public boolean containsText(final String value) {
        WebElement element = getElement();
        return ((element != null) && (element.getText().contains(value)));
    }

    /**
     * Does this element exactly match  given text?
     */
    @Override
    public boolean containsOnlyText(final String value) {
        WebElement element = getElement();
        return ((element != null) && (element.getText().equals(value)));
    }

    /**
     * Does this dropdown contain the specified value.
     */
    @Override
    public boolean containsSelectOption(final String value) {
        return getSelectOptions().contains(value);
    }

    @Override
    public List<String> getSelectOptions() {
        List<WebElement> results = Collections.emptyList();
        if (getElement() != null) {
            results = findElements(By.tagName("option"));
        }
        return convert(results, new ExtractText());
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
        if (driver instanceof ConfigurableTimeouts) {
            ((ConfigurableTimeouts) driver).setImplicitTimeout(implicitTimeout);
        }
    }

    @Override
    public Duration getCurrentImplicitTimeout() {
        if (driver instanceof ConfigurableTimeouts) {
            return ((ConfigurableTimeouts) driver).getCurrentImplicitTimeout();
        }
        return DefaultTimeouts.DEFAULT_IMPLICIT_WAIT_TIMEOUT;
    }

    @Override
    public void resetTimeouts() {
        if (driver instanceof ConfigurableTimeouts) {
            ((ConfigurableTimeouts) driver).resetTimeouts();
        }
    }

    class ExtractText implements Converter<WebElement, String> {
        public String convert(WebElement from) {
            return from.getText();
        }
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
        enableHighlightingIfRequired();
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
        enableHighlightingIfRequired();
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
        getJavascriptExecutorFacade().executeScript("window.focus()");
    }

    @Override
    public WebElementFacade selectByVisibleText(final String label) {
        logIfVerbose("Select label '" + label + "'");
        waitUntilElementAvailable();
        Select select = new Select(getElement());
        select.selectByVisibleText(label);
        notifyScreenChange();
        return this;
    }

    @Override
    public String getSelectedVisibleTextValue() {
        waitUntilVisible();
        Select select = new Select(getElement());
        return select.getFirstSelectedOption().getText();
    }

    @Override
    public WebElementFacade selectByValue(String value) {
        logIfVerbose("Select value '" + value + "'");
        enableHighlightingIfRequired();
        waitUntilElementAvailable();
        Select select = new Select(getElement());
        select.selectByValue(value);
        notifyScreenChange();
        return this;
    }

    @Override
    public String getSelectedValue() {
        waitUntilVisible();
        Select select = new Select(getElement());
        return select.getFirstSelectedOption().getAttribute("value");
    }

    @Override
    public WebElementFacade selectByIndex(int indexValue) {
        logIfVerbose("Select by index '" + indexValue + "'");
        enableHighlightingIfRequired();
        waitUntilElementAvailable();
        Select select = new Select(getElement());
        select.selectByIndex(indexValue);
        notifyScreenChange();
        return this;
    }

    private void waitUntilElementAvailable() {
        if (driverIsDisabled()) {
            return;
        }
        waitUntilEnabled();
    }

    private boolean driverIsDisabled() {
        return StepEventBus.getEventBus().webdriverCallsAreSuspended();
    }

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

    @Override
    public WebElementFacade waitUntilVisible() {
        if (driverIsDisabled()) {
            return this;
        }

        try {
            waitForCondition().until(elementIsDisplayed());
        } catch (Throwable error) {
            if (webElement != null) {
                throwShouldBeVisibleErrorWithCauseIfPresent(error, error.getMessage());
            } else {
                throwNoSuchElementExceptionWithCauseIfPresent(error, error.getMessage());
            }
        }
        return this;
    }

    @Override
    public WebElementFacade waitUntilPresent() {
        if (driverIsDisabled()) {
            return this;
        }

        try {
            waitForCondition().until(elementIsPresent());
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

    private ExpectedCondition<Boolean> elementIsDisplayed() {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return isCurrentlyVisible();
            }
        };
    }

    private ExpectedCondition<Boolean> elementIsPresent() {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return isPresent();
            }
        };
    }

    private ExpectedCondition<Boolean> elementIsNotDisplayed() {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return !isCurrentlyVisible();
            }
        };
    }

    private ExpectedCondition<Boolean> elementIsEnabled() {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                WebElement element = getElement();
                return ((element != null) && (!isDisabledField(element)));
            }
        };
    }
    private ExpectedCondition<Boolean> elementIsClickable() {

        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                WebElement element = getElement();
                return ((element != null) && (element.isDisplayed()) && element.isEnabled());
            }
        };
    }


    private boolean isDisabledField(WebElement webElement) {
        return (isAFormElement(webElement) && (!webElement.isEnabled()));
    }

    private final List<String> HTML_FORM_TAGS = Arrays.asList("input", "button", "select", "textarea", "link", "option");

    private boolean isAFormElement(WebElement webElement) {
        if ((webElement == null) || (webElement.getTagName() == null)) {
            return false;
        }
        String tag = webElement.getTagName().toLowerCase();
        return HTML_FORM_TAGS.contains(tag);

    }

    private static final List<String> HTML_ELEMENTS_WITH_VALUE_ATTRIBUTE = ImmutableList.of("input", "button", "option");

    private boolean hasValueAttribute(WebElement webElement) {
        String tag = webElement.getTagName().toLowerCase();
        return HTML_ELEMENTS_WITH_VALUE_ATTRIBUTE.contains(tag);

    }

    private ExpectedCondition<Boolean> elementIsNotEnabled() {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((getElement() != null) && (!getElement().isEnabled()));
            }
        };
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
        if (driverIsDisabled()) {
            return this;
        }

        try {
            waitForCondition().until(elementIsNotDisplayed());
        } catch (TimeoutException timeout) {
            throwShouldBeInvisibleErrorWithCauseIfPresent(timeout, "Expected hidden element was displayed");
        }
        return this;
    }

    @Override
    public String getValue() {
        waitUntilVisible();
        return getElement().getAttribute("value");
    }

    @Override
    public boolean isSelected() {
        waitUntilVisible();
        return getElement().isSelected();
    }

    @Override
    public String getText() {
        waitUntilVisible();
        return getElement().getText();
    }

    @Override
    public WebElementFacade waitUntilEnabled() {
        if (driverIsDisabled()) {
            return this;
        }

        try {
            waitForCondition().until(elementIsEnabled());
            return this;
        } catch (TimeoutException timeout) {
            throw new ElementShouldBeEnabledException("Expected enabled element was not enabled", timeout);
        }
    }

    @Override
    public WebElementFacade waitUntilClickable() {
        if (driverIsDisabled()) {
            return this;
        }

        try {
            waitForCondition().until(elementIsClickable());
            return this;
        } catch (TimeoutException timeout) {
            throw new ElementShouldBeEnabledException("Expected enabled element was not enabled", timeout);
        }
    }

    @Override
    public WebElementFacade waitUntilDisabled() {
        if (driverIsDisabled()) {
            return this;
        }

        try {
            waitForCondition().until(elementIsNotEnabled());
            return this;
        } catch (TimeoutException timeout) {
            throw new ElementShouldBeDisabledException("Expected disabled element was not disabled", timeout);
        }
    }

    @Override
    public String getTextValue() {
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

    private Optional<String> expectedErrorMessage = Optional.absent();

    protected WebElementState expectingErrorMessage(String errorMessage) {
        this.expectedErrorMessage = Optional.of(errorMessage);
        return this;
    }

    protected String getErrorMessage(String defaultErrorMessage) {
        return expectedErrorMessage.or(defaultErrorMessage);
    }

    private boolean valueAttributeSupportedAndDefinedIn(final WebElement webElement) {
        return hasValueAttribute(webElement) && StringUtils.isNotEmpty(getValue());
    }

    /**
     * Wait for an element to be visible and enabled, and then click on it.
     */
    @Override
    public void click() {
        enableHighlightingIfRequired();
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
            LOGGER.info(humanizedTabfNameFor(getElement()) + ":" + logMessage);
        }
    }

    private boolean useVerboseLogging() {
        return ThucydidesSystemProperty.THUCYDIDES_VERBOSE_STEPS.booleanFrom(environmentVariables);
    }


    private EnvironmentVariables getEnvironmentVariables() {
        return environmentVariables;
    }

    private String humanizedTabfNameFor(WebElement webElement) {
        return HtmlTag.from(webElement).inHumanReadableForm();
    }

    private boolean isMobileDriver() {
        return ThucydidesWebDriverSupport.getDriverClass() == AppiumDriver.class;
    }

    @Override
    public void clear() {

        if (!isMobileDriver()) {
            getElement().sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        }
        getElement().clear();
    }

    private void enableHighlightingIfRequired() {
        JQueryEnabledPage jQueryEnabledPage = JQueryEnabledPage.withDriver(driver);
        if (jQueryEnabledPage.isJQueryIntegrationEnabled() && !jQueryEnabledPage.isJQueryAvailable()) {
            jQueryEnabledPage.injectJQueryPlugins();
        }
    }

    private void notifyScreenChange() {
        StepEventBus.getEventBus().notifyScreenChange();
    }

    @Override
    public String toString() {
        String locatorString = (locator != null) ? locator.toString() : "<Undefined web element>";
        return (resolvedElement() != null) ? resolvedElement().toString() : locatorString;
    }

    public void submit() {
        getElement().submit();
    }

    public void sendKeys(CharSequence... keysToSend) {
        getElement().sendKeys(keysToSend);
    }

    public String getTagName() {
        return getElement().getTagName();
    }

    private Optional<WebDriverFacade> webDriverFacade() {
        if (driver instanceof WebElementFacade) {
            return Optional.of((WebDriverFacade) driver);
        } else {
            return Optional.absent();
        }
    }

    public List<WebElement> findElements(By by) {
        return getElement().findElements(by);
    }

    public WebElement findElement(By by) {
        return getElement().findElement(by);
    }

    public boolean isDisplayed() {
        return getElement().isDisplayed();
    }

    public Point getLocation() {
        return getElement().getLocation();
    }

    public Dimension getSize() {
        return getElement().getSize();
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
}
