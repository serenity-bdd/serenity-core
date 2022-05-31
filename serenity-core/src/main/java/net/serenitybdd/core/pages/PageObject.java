package net.serenitybdd.core.pages;

import com.google.common.base.Predicate;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.WhenPageOpens;
import net.thucydides.core.fluent.ThucydidesFluentAdapter;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.pages.WrongPageError;
import net.thucydides.core.pages.components.Dropdown;
import net.thucydides.core.pages.components.FileToUpload;
import net.thucydides.core.pages.jquery.JQueryEnabledPage;
import net.thucydides.core.reflection.MethodFinder;
import net.thucydides.core.scheduling.FluentWaitWithRefresh;
import net.thucydides.core.scheduling.SerenityFluentWait;
import net.thucydides.core.scheduling.ThucydidesFluentWait;
import net.thucydides.core.steps.PageObjectStepDelayer;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.WaitForBuilder;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;
import net.thucydides.core.webdriver.*;
import net.thucydides.core.webdriver.javascript.JavascriptExecutorFacade;
import net.thucydides.core.webelements.Checkbox;
import net.thucydides.core.webelements.RadioButtonGroup;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.serenitybdd.core.pages.ParameterisedLocator.withArguments;
import static net.serenitybdd.core.selectors.Selectors.xpathOrCssSelector;
import static net.thucydides.core.ThucydidesSystemProperty.*;
import static net.thucydides.core.webdriver.javascript.JavascriptSupport.javascriptIsSupportedIn;

/**
 * A base class representing a WebDriver page object.
 *
 * @author johnsmart
 */
public abstract class PageObject {

    private static final int WAIT_FOR_ELEMENT_PAUSE_LENGTH = 250;

    private static final Logger LOGGER = LoggerFactory.getLogger(PageObject.class);

    private WebDriver driver;

    private Pages pages;

    private MatchingPageExpressions matchingPageExpressions;

    private RenderedPageObjectView renderedView;

    private PageUrls pageUrls;

    private net.serenitybdd.core.time.SystemClock clock;

    private Duration waitForTimeout;
    private Duration waitForElementTimeout;

    private final Sleeper sleeper;
    private final Clock webdriverClock;
    private JavascriptExecutorFacade javascriptExecutorFacade;

    private EnvironmentVariables environmentVariables;

    public static PageObject fromSearchContext(SearchContext searchContext) {
        return null;
    }

    public void setImplicitTimeout(int duration, TemporalUnit unit) {

        waitForElementTimeout = Duration.of(duration, unit);
        setDriverImplicitTimeout(waitForElementTimeout);
    }

    private void setDriverImplicitTimeout(Duration implicitTimeout) {
        if (driver instanceof ConfigurableTimeouts) {
            ((ConfigurableTimeouts) driver).setImplicitTimeout(implicitTimeout);
        } else {
            driver.manage().timeouts().implicitlyWait(implicitTimeout.toMillis(), MILLISECONDS);
        }
    }

    public void resetImplicitTimeout() {
        if (driver instanceof ConfigurableTimeouts) {
            waitForElementTimeout = ((ConfigurableTimeouts) driver).resetTimeouts();
        } else {
            waitForElementTimeout = getDefaultImplicitTimeout();
            driver.manage().timeouts().implicitlyWait(waitForElementTimeout.toMillis(), MILLISECONDS);
        }
    }

    private Duration getDefaultImplicitTimeout() {
        Integer configuredTimeout = ThucydidesSystemProperty.WEBDRIVER_TIMEOUTS_IMPLICITLYWAIT.integerFrom(environmentVariables);
        return Duration.ofMillis(configuredTimeout);
    }

    private enum OpenMode {
        CHECK_URL_PATTERNS,
        IGNORE_URL_PATTERNS
    }

    protected PageObject() {
        this.webdriverClock = Clock.systemDefaultZone();
        this.clock = Injectors.getInjector().getInstance(net.serenitybdd.core.time.SystemClock.class);
        this.environmentVariables = Injectors.getInjector().getProvider(EnvironmentVariables.class).get();
        this.sleeper = Sleeper.SYSTEM_SLEEPER;
    }

    protected PageObject(final WebDriver driver, Predicate<? super PageObject> callback) {
        this();
        this.driver = driver;
        callback.apply(this);
    }

    public PageObject(final WebDriver driver, final int ajaxTimeout) {
        this();
        setDriver(driver, ajaxTimeout);
    }

    public PageObject(final WebDriver driver) {
        this();
        ThucydidesWebDriverSupport.useDriver(driver);
        setDriver(driver);
    }

    public PageObject(final WebDriver driver, final EnvironmentVariables environmentVariables) {
        this();
        this.environmentVariables = environmentVariables;
        setDriver(driver);
    }

    protected void setDriver(WebDriver driver, long timeout) {
        this.driver = driver;
        new DefaultPageObjectInitialiser(driver, timeout).apply(this);
    }

    public <T extends PageObject> T setDriver(WebDriver driver) {
        setDriver(driver, getImplicitWaitTimeout().toMillis());
        return (T) this;
    }

    public <T extends PageObject> T withDriver(WebDriver driver) {
        return setDriver(driver);
    }

    public Duration getWaitForTimeout() {

        if (waitForTimeout == null) {
            int configuredWaitForTimeoutInMilliseconds = fluentWaitTimeout();
            waitForTimeout = Duration.ofMillis(configuredWaitForTimeoutInMilliseconds);
        }
        return waitForTimeout;
    }

    private int fluentWaitTimeout() {
        return (WEBDRIVER_WAIT_FOR_TIMEOUT.integerFrom(environmentVariables,
                WEBDRIVER_TIMEOUTS_FLUENTWAIT.integerFrom(environmentVariables,
                        (int) DefaultTimeouts.DEFAULT_WAIT_FOR_TIMEOUT.toMillis())));
    }

    @Deprecated
    public Duration getWaitForElementTimeout() {
        return getImplicitWaitTimeout();
    }

    public Duration getImplicitWaitTimeout() {

        if (waitForElementTimeout == null) {
            int configuredWaitForTimeoutInMilliseconds =
                    ThucydidesSystemProperty.WEBDRIVER_TIMEOUTS_IMPLICITLYWAIT
                            .integerFrom(environmentVariables, (int) DefaultTimeouts.DEFAULT_IMPLICIT_WAIT_TIMEOUT.toMillis());
            waitForElementTimeout = Duration.ofMillis(configuredWaitForTimeoutInMilliseconds);
        }
        return waitForElementTimeout;
    }

    public void setPages(Pages pages) {
        this.pages = pages;
    }

    @Deprecated
    public <T extends PageObject> T switchToPage(final Class<T> pageObjectClass) {
        if (pages.getDriver() == null) {
            pages.setDriver(driver);
        }

        return pages.getPage(pageObjectClass);
    }

    /**
     * Upload a file via an HTML form.
     * By default, this will look for a file on the file system, at the location provided.
     */
    public FileToUpload upload(final String filename) {
        return new FileToUpload(driver, filename).useRemoteDriver(isDefinedRemoteUrl());
    }

    public FileToUpload uploadData(String data) throws IOException {
        Path datafile = Files.createTempFile("upload", "data");
        Files.write(datafile, data.getBytes(StandardCharsets.UTF_8));
        return new FileToUpload(driver, datafile.toAbsolutePath().toString()).useRemoteDriver(isDefinedRemoteUrl());
    }

    public FileToUpload uploadData(byte[] data) throws IOException {
        Path datafile = Files.createTempFile("upload", "data");
        Files.write(datafile, data);
        return new FileToUpload(driver, datafile.toAbsolutePath().toString()).useRemoteDriver(isDefinedRemoteUrl());
    }

    private boolean isDefinedRemoteUrl() {
        boolean isRemoteUrl = ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL.isDefinedIn(environmentVariables);
        boolean isBrowserStack = ThucydidesSystemProperty.BROWSERSTACK_URL.isDefinedIn(environmentVariables);
        return isRemoteUrl || isBrowserStack;
    }

    private PageUrls getPageUrls() {
        if (pageUrls == null) {
            pageUrls = new PageUrls(this, environmentVariables);
        }
        return pageUrls;
    }

    /**
     * Only for testing purposes.
     */
    public void setPageUrls(PageUrls pageUrls) {
        this.pageUrls = pageUrls;
    }

    public void setWaitForTimeout(final long waitForTimeoutInMilliseconds) {
        this.waitForTimeout = Duration.ofMillis(waitForTimeoutInMilliseconds);
        getRenderedView().setWaitForTimeout(this.waitForTimeout);
    }

    public void setWaitForElementTimeout(final long waitForTimeoutInMilliseconds) {
        this.waitForElementTimeout = Duration.ofMillis(waitForTimeoutInMilliseconds);
    }

    protected RenderedPageObjectView getRenderedView() {
        if (renderedView == null) {
            renderedView = new RenderedPageObjectView(driver, this, getWaitForTimeout(), true);
        }
        return renderedView;
    }

    protected net.serenitybdd.core.time.SystemClock getClock() {
        return clock;
    }

    private MatchingPageExpressions getMatchingPageExpressions() {
        if (matchingPageExpressions == null) {
            matchingPageExpressions = new MatchingPageExpressions(this);
        }
        return matchingPageExpressions;
    }

    public WebDriver getDriver() {
        if (driver == null) {
            driver = Serenity.getDriver();
        }
        return driver;
    }

    /**
     * Determines if the current driver is equipped with Chrome Dev Tools
     */
    public boolean hasDevTools() {
        if (getDriver() instanceof WebDriverFacade) {
            return (((WebDriverFacade) getDriver()).getProxiedDriver() instanceof HasDevTools);
        } else {
            return (getDriver() instanceof HasDevTools);
        }
    }

    public Optional<DevTools> maybeGetDevTools() {
       return Optional.ofNullable(getDevTools());
    }

    public DevTools getDevTools() {
        if (!hasDevTools()) {
            return null;
        }
        if (getDriver() instanceof WebDriverFacade) {
            return ((HasDevTools) ((WebDriverFacade) getDriver()).getProxiedDriver()).getDevTools();
        } else {
            return ((HasDevTools) getDriver()).getDevTools();
        }
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public boolean matchesAnyUrl() {
        return thereAreNoPatternsDefined();
    }

    /**
     * Does this page object work for this URL? When matching a URL, we check
     * with and without trailing slashes
     */
    public final boolean compatibleWithUrl(final String currentUrl) {
        return thereAreNoPatternsDefined() || matchUrlAgainstEachPattern(currentUrl);
    }

    private boolean matchUrlAgainstEachPattern(final String currentUrl) {
        return getMatchingPageExpressions().matchUrlAgainstEachPattern(currentUrl);
    }

    private boolean thereAreNoPatternsDefined() {
        return getMatchingPageExpressions().isEmpty();
    }

    public PageObject waitForRenderedElements(final By byElementCriteria) {
        getRenderedView().waitFor(byElementCriteria);
        return this;
    }

    /**
     * @deprecated TimeUnit has been replaced by TemporalUnit in Selenium. For more consistancy use a TemporalUnit parameter.
     */
    @Deprecated
    public RenderedPageObjectView withTimeoutOf(int timeout, TimeUnit units) {
        return withTimeoutOf(Duration.of(timeout, TemporalUnitConverter.fromTimeUnit(units)));
    }

    public RenderedPageObjectView withTimeoutOf(int timeout, TemporalUnit units) {
        return withTimeoutOf(Duration.of(timeout, units));
    }

    public RenderedPageObjectView withTimeoutOf(Duration timeout) {
        return new RenderedPageObjectView(driver, this, timeout, false);
    }

    /**
     * Alternative to withTimeoutOf()
     */
    public RenderedPageObjectView waitingForNoLongerThan(int timeout, TimeUnit units) {
        return withTimeoutOf(Duration.of(timeout, TemporalUnitConverter.fromTimeUnit(units)));
    }

    /**
     * Alternative to withTimeoutOf() using a DSL
     */
    public WaitingBuilder waitingForNoLongerThan(int timeout) {
        return new WaitingBuilder(timeout, this);
    }

    public static class WaitingBuilder {

        private final int timeout;
        private final PageObject page;

        public WaitingBuilder(int timeout, PageObject page) {
            this.timeout = timeout;
            this.page = page;
        }

        public RenderedPageObjectView milliseconds() {
            return page.withTimeoutOf(Duration.ofMillis(timeout));
        }
        public RenderedPageObjectView seconds() {
            return page.withTimeoutOf(Duration.ofSeconds(timeout));
        }
        public RenderedPageObjectView minutes() {
            return page.withTimeoutOf(Duration.ofMinutes(timeout));
        }
    }


    public PageObject waitFor(String xpathOrCssSelector, Object firstArgument, Object... arguments) {
        List<Object> args = new ArrayList<>();
        args.add(firstArgument);
        args.addAll(Arrays.asList(arguments));
        return waitForRenderedElements(xpathOrCssSelector(withArguments(xpathOrCssSelector, args.toArray())));
    }

    public PageObject waitFor(String xpathOrCssSelector) {
        return waitForRenderedElements(xpathOrCssSelector(xpathOrCssSelector));
    }

    public <T> T waitFor(ExpectedCondition<T> expectedCondition) {
        return getRenderedView().waitFor(expectedCondition);
    }

    public <T> T waitFor(String message, ExpectedCondition<T> expectedCondition) {
        return getRenderedView().waitFor(message, expectedCondition);
    }

    public PageObject waitForRenderedElementsToBePresent(final By byElementCriteria) {
        getRenderedView().waitForPresenceOf(byElementCriteria);

        return this;
    }

    public PageObject waitForPresenceOf(String xpathOrCssSelector, Object... arguments) {
        return waitForRenderedElementsToBePresent(xpathOrCssSelector(withArguments(xpathOrCssSelector, arguments)));
    }

    public PageObject waitForRenderedElementsToDisappear(final By byElementCriteria) {
        getRenderedView().waitForElementsToDisappear(byElementCriteria);
        return this;
    }

    public PageObject waitForAbsenceOf(String xpathOrCssSelector, Object... arguments) {
        return waitForRenderedElementsToDisappear(xpathOrCssSelector(withArguments(xpathOrCssSelector, arguments)));
    }

    public PageObject waitForAbsenceOf(By byLocator) {
        return waitForRenderedElementsToDisappear(byLocator);
    }

    /**
     * Waits for a given text to appear anywhere on the page.
     */
    public PageObject waitForTextToAppear(final String expectedText) {
        getRenderedView().waitForText(expectedText);
        return this;
    }

    public PageObject waitForTitleToAppear(final String expectedTitle) {
        waitOnPage().until(ExpectedConditions.titleIs(expectedTitle));
        return this;
    }

    public WebDriverWait waitOnPage() {
        return new WebDriverWait(driver, getWaitForTimeout());
    }

    public PageObject waitForTitleToDisappear(final String expectedTitle) {
        getRenderedView().waitForTitleToDisappear(expectedTitle);
        return this;
    }

    /**
     * Waits for a given text to appear inside the element.
     */
    public PageObject waitForTextToAppear(final WebElement element,
                                          final String expectedText) {
        getRenderedView().waitForText(element, expectedText);
        return this;
    }

    private boolean driverIsDisabled() {
        return StepEventBus.getEventBus().webdriverCallsAreSuspended();
    }

    /**
     * Waits for a given text to disappear from the element.
     */
    public PageObject waitForTextToDisappear(final WebElement element,
                                             final String expectedText) {
        if (!driverIsDisabled()) {
            waitForCondition().until(elementDoesNotContain(element, expectedText));
        }
        return this;
    }

    private ExpectedCondition<Boolean> elementDoesNotContain(final WebElement element, final String expectedText) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return !element.getText().contains(expectedText);
            }
        };
    }

    public PageObject waitForTextToDisappear(final String expectedText) {
        return waitForTextToDisappear(expectedText, getWaitForTimeout().toMillis());
    }

    /**
     * Waits for a given text to not be anywhere on the page.
     */
    public PageObject waitForTextToDisappear(final String expectedText,
                                             final long timeoutInMilliseconds) {

        getRenderedView().waitForTextToDisappear(expectedText, timeoutInMilliseconds);
        return this;
    }

    /**
     * Waits for a given text to appear anywhere on the page.
     */
    public PageObject waitForTextToAppear(final String expectedText,
                                          final long timeout) {

        getRenderedView().waitForTextToAppear(expectedText, timeout);
        return this;
    }

    /**
     * Waits for any of a number of text blocks to appear anywhere on the
     * screen.
     */
    public PageObject waitForAnyTextToAppear(final String... expectedText) {
        getRenderedView().waitForAnyTextToAppear(expectedText);
        return this;
    }

    public PageObject waitForAnyTextToAppear(final WebElement element,
                                             final String... expectedText) {
        getRenderedView().waitForAnyTextToAppear(element, expectedText);
        return this;
    }

    /**
     * Waits for all of a number of text blocks to appear on the screen.
     */
    public PageObject waitForAllTextToAppear(final String... expectedTexts) {
        getRenderedView().waitForAllTextToAppear(expectedTexts);
        return this;
    }

    public PageObject waitForAnyRenderedElementOf(final By... expectedElements) {
        getRenderedView().waitForAnyRenderedElementOf(expectedElements);
        return this;
    }

    protected void waitABit(final long timeInMilliseconds) {
        getClock().pauseFor(timeInMilliseconds);
    }

    public WaitForBuilder<? extends PageObject> waitFor(int duration) {
        return new PageObjectStepDelayer(clock, this).waitFor(duration);
    }

    public List<WebElement> thenReturnElementList(final By byListCriteria) {
        return driver.findElements(byListCriteria);
    }

    /**
     * Check that the specified text appears somewhere in the page.
     */
    public void shouldContainText(final String textValue) {
        if (!containsText(textValue)) {
            String errorMessage = String.format(
                    "The text '%s' was not found in the page", textValue);
            throw new NoSuchElementException(errorMessage);
        }
    }

    /**
     * Check that all of the specified texts appears somewhere in the page.
     */
    public void shouldContainAllText(final String... textValues) {
        if (!containsAllText(textValues)) {
            String errorMessage = String.format(
                    "One of the text elements in '%s' was not found in the page", (Object[]) textValues);
            throw new NoSuchElementException(errorMessage);
        }
    }

    /**
     * Does the specified web element contain a given text value. Useful for dropdowns and so on.
     *
     * @deprecated use element(webElement).containsText(textValue)
     */
    @Deprecated
    public boolean containsTextInElement(final WebElement webElement, final String textValue) {
        return element(webElement).containsText(textValue);
    }

    /*
     * Check that the element contains a given text.
     * @deprecated use element(webElement).shouldContainText(textValue)
     */
    @Deprecated
    public void shouldContainTextInElement(final WebElement webElement, final String textValue) {
        element(webElement).shouldContainText(textValue);
    }

    /*
     * Check that the element does not contain a given text.
     * @deprecated use element(webElement).shouldNotContainText(textValue)
     */
    @Deprecated
    public void shouldNotContainTextInElement(final WebElement webElement, final String textValue) {
        element(webElement).shouldNotContainText(textValue);
    }

    /**
     * Clear a field and enter a value into it.
     */
    public void typeInto(final WebElement field, final String value) {
        element(field).type(value);
    }

    /**
     * Clear a field and enter a value into it.
     * This is a more fluent alternative to using the typeInto method.
     */
    public FieldEntry enter(CharSequence... keysToSend) {
        return new FieldEntry(keysToSend);
    }

    public void selectFromDropdown(final WebElement dropdown,
                                   final String visibleLabel) {

        Dropdown.forWebElement(dropdown).select(visibleLabel);
        notifyScreenChange();
    }

    public void selectMultipleItemsFromDropdown(final WebElement dropdown,
                                                final String... selectedLabels) {
        Dropdown.forWebElement(dropdown).selectMultipleItems(selectedLabels);
        notifyScreenChange();
    }

    public Set<String> getSelectedOptionLabelsFrom(final WebElement dropdown) {
        return Dropdown.forWebElement(dropdown).getSelectedOptionLabels();
    }

    public Set<String> getSelectedOptionValuesFrom(final WebElement dropdown) {
        return Dropdown.forWebElement(dropdown).getSelectedOptionValues();
    }

    public String getSelectedValueFrom(final WebElement dropdown) {
        return Dropdown.forWebElement(dropdown).getSelectedValue();
    }

    public String getSelectedLabelFrom(final WebElement dropdown) {
        return Dropdown.forWebElement(dropdown).getSelectedLabel();
    }

    public void setCheckbox(final WebElement field, final boolean value) {
        Checkbox checkbox = new Checkbox(field);
        checkbox.setChecked(value);
        notifyScreenChange();
    }

    public boolean containsText(final String textValue) {
        return getRenderedView().containsText(textValue);
    }

    /**
     * Check that the specified text appears somewhere in the page.
     */
    public boolean containsAllText(final String... textValues) {
        for (String textValue : textValues) {
            if (!getRenderedView().containsText(textValue)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Fail the test if this element is not displayed (rendered) on the screen.
     */
    public void shouldBeVisible(final WebElement field) {
        element(field).shouldBeVisible();
    }

    public void shouldBeVisible(final By byCriteria) {
        waitOnPage().until(ExpectedConditions.visibilityOfElementLocated(byCriteria));
    }

    public void shouldNotBeVisible(final WebElement field) {
        try {
            element(field).shouldNotBeVisible();
        } catch (NoSuchElementException e) {
            // A non-existant element is not visible
        }
    }

    public void shouldNotBeVisible(final By byCriteria) {
        List<WebElement> matchingElements = getDriver().findElements(byCriteria);
        if (!matchingElements.isEmpty()) {
            waitOnPage().until(ExpectedConditions.invisibilityOfElementLocated(byCriteria));
        }
    }

    public long waitForTimeoutInMilliseconds() {
        return getWaitForTimeout().toMillis();
    }

    public long implicitTimoutMilliseconds() {
        return getImplicitWaitTimeout().toMillis();
    }

    public String updateUrlWithBaseUrlIfDefined(String startingUrl) {

        if (getPageUrls().getDeclaredDefaultUrl().isPresent()) {
            startingUrl = getPageUrls().addDefaultUrlTo(startingUrl);
        }

        String baseUrl = getPageUrls().getSystemBaseUrl();
        if (isDefined(baseUrl)) {
            if (isFullUrl(startingUrl)) {
                return replaceHost(startingUrl, baseUrl);
            } else if (isRelative(startingUrl)) {
                return getPageUrls().addBaseUrlTo(startingUrl);
            }
        }

        return startingUrl;
    }

    protected boolean isRelative(String startingUrl) {
        return !(isFullUrl(startingUrl));
    }

    private boolean isDefined(String url) {
        return (url != null) && (!StringUtils.isEmpty(url));
    }

    private boolean isFullUrl(String startingUrl) {
        try {
            new URL(startingUrl);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private String replaceHost(final String starting, final String base) {

        String updatedUrl = starting;
        try {
            URL startingUrl = new URL(starting);
            URL baseUrl = new URL(base);

            String startingHostComponent = hostComponentFrom(startingUrl.getProtocol(),
                    startingUrl.getHost(),
                    startingUrl.getPort());
            String baseHostComponent = hostComponentFrom(baseUrl.getProtocol(),
                    baseUrl.getHost(),
                    baseUrl.getPort());
            updatedUrl = starting.replaceFirst(startingHostComponent, baseHostComponent);
        } catch (MalformedURLException e) {
            LOGGER.error("Failed to analyse default page URL: Starting URL: {}, Base URL: {}", starting, base);
            LOGGER.error("URL analysis failed with exception:", e);
        }

        return updatedUrl;
    }

    private String hostComponentFrom(final String protocol, final String host, final int port) {
        StringBuilder hostComponent = new StringBuilder(protocol);
        hostComponent.append("://");
        hostComponent.append(host);
        if (port > 0) {
            hostComponent.append(":");
            hostComponent.append(port);
        }
        return hostComponent.toString();
    }

    /**
     * Open the webdriver browser using a paramaterized URL. Parameters are
     * represented in the URL using {0}, {1}, etc.
     */
    public final void open(final String[] parameterValues) {
        open(OpenMode.CHECK_URL_PATTERNS, parameterValues);
    }

    /**
     * Opens page without checking URL patterns. Same as open(String...)) otherwise.
     */
    public final void openUnchecked(final String... parameterValues) {
        open(OpenMode.IGNORE_URL_PATTERNS, parameterValues);
    }

    private void open(final OpenMode openMode, final String... parameterValues) {
        String startingUrl = getPageUrls().getStartingUrl(parameterValues);
        LOGGER.debug("Opening page at url {}", startingUrl);
        openPageAtUrl(startingUrl);
        checkUrlPatterns(openMode);
        initializePage();
        LOGGER.debug("Page opened");
    }

    public final OpenWithParams open(final String urlTemplateName) {
        return new OpenWithParams(this, urlTemplateName);
    }

    public static class OpenWithParams {

        private PageObject pageObject;
        private String urlTemplateName;

        public OpenWithParams(PageObject pageObject, String urlTemplateName) {
            this.pageObject = pageObject;
            this.urlTemplateName = urlTemplateName;
        }

        public void withParameters(String... parameters) {
            pageObject.open(urlTemplateName, parameters);
        }
    }

    public final void open(final String urlTemplateName,
                           final String[] parameterValues) {
        open(OpenMode.CHECK_URL_PATTERNS, urlTemplateName, parameterValues);
    }

    /**
     * Opens page without checking URL patterns. Same as {@link #open(String, String[])} otherwise.
     */
    public final void openUnchecked(final String urlTemplateName,
                                    final String[] parameterValues) {
        open(OpenMode.IGNORE_URL_PATTERNS, urlTemplateName, parameterValues);
    }

    private void open(final OpenMode openMode, final String urlTemplateName,
                      final String[] parameterValues) {
        String startingUrl = getPageUrls().getNamedUrl(urlTemplateName, parameterValues);
        LOGGER.debug("Opening page at url {}", startingUrl);
        openPageAtUrl(startingUrl);
        checkUrlPatterns(openMode);
        initializePage();
        LOGGER.debug("Page opened");
    }

    /**
     * Open the webdriver browser to the base URL, determined by the DefaultUrl
     * annotation if present. If the DefaultUrl annotation is not present, the
     * default base URL will be used. If the DefaultUrl annotation is present, a
     * URL based on the current base url from the system-wide default url
     * and the relative path provided in the DefaultUrl annotation will be used to
     * determine the URL to open. For example, consider the following class:
     * <pre>
     *     <code>
     *         &#064;DefaultUrl("http://localhost:8080/client/list")
     *         public class ClientList extends PageObject {
     *             ...
     *
     *             &#064;WhenPageOpens
     *             public void waitUntilTitleAppears() {...}
     *         }
     *     </code>
     * </pre>
     * Suppose you are using a base URL of http://stage.acme.com. When you call open() for this class,
     * it will open http://stage.acme.com/client/list. It will then invoke the waitUntilTitleAppears() method.
     */
    final public void open() {
        open(OpenMode.CHECK_URL_PATTERNS);
    }

    /**
     * Opens page without checking URL patterns. Same as {@link #open()} otherwise.
     */
    final public void openUnchecked() {
        open(OpenMode.IGNORE_URL_PATTERNS);
    }

    private void open(final OpenMode openMode) {
        String startingUrl = updateUrlWithBaseUrlIfDefined(getPageUrls().getStartingUrl());
        openPageAtUrl(startingUrl);
        checkUrlPatterns(openMode);
        initializePage();
    }

    private void initializePage() {
        addJQuerySupport();
        callWhenPageOpensMethods();
    }

    private void checkUrlPatterns(final OpenMode openMode) {
        if (openMode == OpenMode.CHECK_URL_PATTERNS) {
            ensurePageIsOnAMatchingUrl();
        }
    }

    private void ensurePageIsOnAMatchingUrl() {
        if (!matchesAnyUrl()) {
            String currentUrl = getDriver().getCurrentUrl();
            if (!compatibleWithUrl(currentUrl)) {
                thisIsNotThePageYourLookingFor();
            }
        }
    }

    /**
     * Use the @At annotation (if present) to check that a page object is displaying the correct page.
     * Will throw an exception if the current URL does not match the expected one.
     */
    public void shouldBeDisplayed() {
        ensurePageIsOnAMatchingUrl();
    }

    private void thisIsNotThePageYourLookingFor() {

        String errorDetails = "This is not the page you're looking for: "
                              + "I was looking for a page compatible with " + this.getClass() + " but "
                              + "I was at the URL " + getDriver().getCurrentUrl();

        throw new WrongPageError(errorDetails);
    }

    final public void openAt(String relativeUrl) {
        openPageAtUrl(updateUrlWithBaseUrlIfDefined(relativeUrl));
        callWhenPageOpensMethods();
    }

    final public void openUrl(String absoluteUrl) {
        openPageAtUrl(absoluteUrl);
        callWhenPageOpensMethods();
    }

    /**
     * Override this method
     */
    public void callWhenPageOpensMethods() {
        if (StepEventBus.getEventBus().currentTestIsSuspended()) {
            return;
        }

        for (Method annotatedMethod : methodsAnnotatedWithWhenPageOpens()) {
            try {
                annotatedMethod.setAccessible(true);
                annotatedMethod.invoke(this);
            } catch (Throwable e) {
                LOGGER.error("Could not execute @WhenPageOpens annotated method: " + e.getMessage());
                if (e instanceof InvocationTargetException) {
                    e = ((InvocationTargetException) e).getTargetException();
                }
                if (AssertionError.class.isAssignableFrom(e.getClass())) {
                    throw (AssertionError) e;
                } else {
                    throw new UnableToInvokeWhenPageOpensMethods("Could not execute @WhenPageOpens annotated method: "
                                                                 + e.getMessage(), e);
                }
            }
        }
    }

    private List<Method> methodsAnnotatedWithWhenPageOpens() {
        List<Method> methods = MethodFinder.inClass(this.getClass()).getAllMethods();
        List<Method> annotatedMethods = new ArrayList<>();
        for (Method method : methods) {
            if (method.getAnnotation(WhenPageOpens.class) != null) {
                if (method.getParameterTypes().length == 0) {
                    annotatedMethods.add(method);
                } else {
                    throw new UnableToInvokeWhenPageOpensMethods(
                            "Could not execute @WhenPageOpens annotated method: WhenPageOpens method cannot have parameters: " +
                            method);
                }
            }
        }
        return annotatedMethods;
    }

    public static String[] withParameters(final String... parameterValues) {
        return parameterValues;
    }

    private void openPageAtUrl(final String startingUrl) {
        String url = NormalizeUrlForm.ofUrl(startingUrl);
        getDriver().get(url);
        if (javascriptIsSupportedIn(getDriver())) {
            addJQuerySupport();
        }
    }

    /**
     * Open an environment-specific page defined in the `serenity.conf` file under the `pages` section.
     *
     * @param pageName
     */
    public void openPageNamed(String pageName) {
        getDriver().get(environmentSpecificPageUrl(pageName));
    }

    public void navigateToPageNamed(String pageName) {
        getDriver().navigate().to(environmentSpecificPageUrl(pageName));
    }

    private String environmentSpecificPageUrl(String pageName) {
        return EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty("pages." + pageName)
                .orElseThrow(() -> new NoSuchPageException(
                        "No page called " + pageName + " was specified in the serenity.conf file"));
    }

    public void clickOn(final WebElement webElement) {
        element(webElement).click();
    }

    /**
     * Returns true if at least one matching element is found on the page and is visible.
     */
    public Boolean isElementVisible(final By byCriteria) {
        return getRenderedView().elementIsDisplayed(byCriteria);
    }

    public void setDefaultBaseUrl(final String defaultBaseUrl) {
        getPageUrls().overrideDefaultBaseUrl(defaultBaseUrl);
    }

    /**
     * Returns true if the specified element has the focus.
     *
     * @deprecated Use element(webElement).hasFocus() instead
     */
    public boolean hasFocus(final WebElement webElement) {
        return element(webElement).hasFocus();
    }

    public void blurActiveElement() {
        getJavascriptExecutorFacade().executeScript("document.activeElement.blur();");
    }

    protected JavascriptExecutorFacade getJavascriptExecutorFacade() {
        if (javascriptExecutorFacade == null) {
            javascriptExecutorFacade = new JavascriptExecutorFacade(driver);
        }
        return javascriptExecutorFacade;
    }

    /**
     * Provides a fluent API for querying web elements.
     */
    public <T extends net.serenitybdd.core.pages.WebElementFacade> T element(WebElement webElement) {
        return net.serenitybdd.core.pages.WebElementFacadeImpl.wrapWebElement(driver, webElement,
                getImplicitWaitTimeout().toMillis(),
                getWaitForTimeout().toMillis(),
                nameOf(webElement));
    }

    private String nameOf(WebElement webElement) {
        try {
            return webElement.toString();
        } catch (Exception e) {
            return "Unknown web element";
        }
    }

    public <T extends net.serenitybdd.core.pages.WebElementFacade> T $(WithLocator locator) {
        return element(locator.getLocator());
    }

    public <T extends net.serenitybdd.core.pages.WebElementFacade> T $(WithByLocator locator) {
        return element(locator.getLocator());
    }

    public <T extends net.serenitybdd.core.pages.WebElementFacade> T $(WebElement webElement) {
        return element(webElement);
    }

    public <T extends net.serenitybdd.core.pages.WebElementFacade> T $(String xpathOrCssSelector, Object... arguments) {
        return element(xpathOrCssSelector, arguments);
    }

    public <T extends net.serenitybdd.core.pages.WebElementFacade> T $(By bySelector) {
        return element(bySelector);
    }

    public net.serenitybdd.core.pages.WebElementFacade $(ResolvableElement selector) {
        return find(selector);
    }

    public ListOfWebElementFacades $$(ResolvableElement selector) {
        return findAll(selector);
    }

    /**
     * Return the text value of a given element
     */
    public String textOf(WithLocator locator) {
        return $(locator).getText();
    }

    public String textOf(WithByLocator locator) {
        return $(locator).getText();
    }

    public String textOf(String xpathOrCssSelector, Object... arguments) {
        return $(xpathOrCssSelector, arguments).getText();
    }

    public String textOf(By bySelector) {
        return $(bySelector).getText();
    }

    /**
     * Return the text value of a given element
     */
    public String textContentOf(WithLocator locator) {
        return $(locator).getTextContent();
    }

    public String textContentOf(WithByLocator locator) {
        return $(locator).getTextContent();
    }

    public String textContentOf(String xpathOrCssSelector, Object... arguments) {
        return $(xpathOrCssSelector, arguments).getTextContent();
    }

    public String textContentOf(By bySelector) {
        return $(bySelector).getTextContent();
    }

    public ListOfWebElementFacades $$(String xpathOrCssSelector, Object... arguments) {
        return findAll(xpathOrCssSelector, arguments);
    }

    public ListOfWebElementFacades $$(By bySelector) {
        return findAll(bySelector);
    }

    /**
     * Provides a fluent API for querying web elements.
     */
    public <T extends net.serenitybdd.core.pages.WebElementFacade> T element(By bySelector) {
        return net.serenitybdd.core.pages.WebElementFacadeImpl.wrapWebElement(driver,
                bySelector,
                getImplicitWaitTimeout().toMillis(),
                getWaitForTimeout().toMillis(),
                bySelector.toString());
    }

    public <T extends net.serenitybdd.core.pages.WebElementFacade> T find(By selector) {
        return element(selector);
    }

    public net.serenitybdd.core.pages.WebElementFacade find(ResolvableElement selector) {
        return selector.resolveFor(this);
    }

    public ListOfWebElementFacades findAll(ResolvableElement selector) {
        return findAllWithRetry((page) -> selector.resolveAllFor(this));
    }

    public <T extends net.serenitybdd.core.pages.WebElementFacade> T find(WithByLocator selector) {
        return element(selector.getLocator());
    }

    public <T extends net.serenitybdd.core.pages.WebElementFacade> T find(WithLocator selector) {
        return element(selector.getLocator());
    }

    public <T extends net.serenitybdd.core.pages.WebElementFacade> T find(List<By> selectors) {
        T element = null;
        for (By selector : selectors) {
            if (element == null) {
                element = element(selector);
            } else {
                element = element.find(selector);
            }
        }
        return element;
    }

    public <T extends net.serenitybdd.core.pages.WebElementFacade> T findBy(List<String> selectors) {
        T element = null;
        for (String selector : selectors) {
            if (element == null) {
                element = element(selector);
            } else {
                element = element.findBy(selector);
            }
        }
        return element;
    }

    public <T extends net.serenitybdd.core.pages.WebElementFacade> T findNested(By... selectors) {
        T element = null;
        for (By selector : selectors) {
            if (element == null) {
                element = element(selector);
            } else {
                element = element.findBy(selector);
            }
        }
        return element;
    }

    public <T extends net.serenitybdd.core.pages.WebElementFacade> T find(String selector) {
        return findBy(NewList.of(selector));
    }

    public <T extends net.serenitybdd.core.pages.WebElementFacade> T findNested(String... selectors) {
        return findBy(NewList.of(selectors));
    }

    public Optional<WebElementFacade> findFirst(String xpathOrCSSSelector) {
        return findEach(xpathOrCSSSelector).findFirst();
    }

    public Optional<WebElementFacade> findFirst(By bySelector) {
        return findEach(bySelector).findFirst();
    }

    public Stream<WebElementFacade> findEach(By bySelector) {
        return findAll(bySelector).stream();
    }

    public Stream<WebElementFacade> findEach(WithByLocator bySelector) {
        return findAll(bySelector.getLocator()).stream();
    }

    public Stream<WebElementFacade> findEach(WithLocator bySelector) {
        return findAll(bySelector.getLocator()).stream();
    }

    /**
     * FindEach will return a stream of WebElementFacades matching the described nested structure.
     * Only the last selector will return a list; the initial selectors will be used to locate the list of elements.
     *
     * @param bySelectors
     * @return
     */
    public Stream<WebElementFacade> findEach(By... bySelectors) {
        if (bySelectors.length == 1) {
            return findEach(bySelectors[0]);
        }
        return find(allButLastIn(bySelectors))
                .thenFindAll(lastIn(bySelectors)).stream();
    }

    public Stream<WebElementFacade> findEach(String... xpathOrCssSelectors) {
        if (xpathOrCssSelectors.length == 1) {
            return findEach(xpathOrCssSelectors[0]);
        }
        return findBy(allButLastIn(xpathOrCssSelectors))
                .thenFindAll(lastIn(xpathOrCssSelectors)).stream();
    }

    public ListOfWebElementFacades findNestedElements(String... xpathOrCssSelectors) {
        if (xpathOrCssSelectors.length == 1) {
            return findAll(xpathOrCssSelectors[0]);
        }
        return findBy(allButLastIn(xpathOrCssSelectors))
                .thenFindAll(lastIn(xpathOrCssSelectors));
    }

    private <T> List<T> allButLastIn(T[] selectors) {
        List<T> subList = new ArrayList<>();
        for (int i = 0; i < selectors.length - 1; i++) {
            subList.add(selectors[i]);
        }
        return subList;
    }

    private <T> T lastIn(T[] selectors) {
        return selectors[selectors.length - 1];
    }

    public Stream<WebElementFacade> findEach(String xpathOrCSSSelector) {
        return findAll(xpathOrCSSSelector).stream();
    }

    public ListOfWebElementFacades findAll(By bySelector) {
        return findAllWithRetry(page -> {
            List<WebElementFacade> matchingWebElements = new ArrayList<>();
            for (WebElement webElement : driver.findElements(bySelector)) {
                WebElementFacade element = element(webElement);
                matchingWebElements.add(element);
            }
            return new ListOfWebElementFacades(matchingWebElements);
        });
    }

    public ListOfWebElementFacades findAll(WithLocator bySelector) {
        return findAllWithRetry((page) -> page.findAll(bySelector.getLocator()));
    }

    public ListOfWebElementFacades findAll(WithByLocator bySelector) {
        return findAllWithRetry((page) -> page.findAll(bySelector.getLocator()));
    }

    /**
     * Provides a fluent API for querying web elements.
     */
    public <T extends net.serenitybdd.core.pages.WebElementFacade> T element(String xpathOrCssSelector, Object... arguments) {
        return element(xpathOrCssSelector(withArguments(xpathOrCssSelector, arguments)));
    }

    public <T extends net.serenitybdd.core.pages.WebElementFacade> T findBy(String xpathOrCssSelector, Object... arguments) {
        return element(withArguments(xpathOrCssSelector, arguments));
    }

    public Optional<WebElementFacade> findFirst(String xpathOrCssSelector, Object... arguments) {
        return findAll(xpathOrCssSelector, arguments).stream().findFirst();
    }

    public ListOfWebElementFacades findAll(String xpathOrCssSelector, Object... arguments) {
        return findAllWithRetry((page) -> page.findAll(xpathOrCssSelector(withArguments(xpathOrCssSelector, arguments))));
    }

    public boolean containsElements(By bySelector) {
        return !findAll(bySelector).isEmpty();
    }

    public boolean containsElements(String xpathOrCssSelector, Object... arguments) {
        return !findAll(xpathOrCssSelector, arguments).isEmpty();
    }

    public Object evaluateJavascript(final String script) {
        addJQuerySupport();
        JavascriptExecutorFacade js = new JavascriptExecutorFacade(driver);
        return js.executeScript(script);
    }

    public Object evaluateAsyncJavascript(final String script) {
        addJQuerySupport();
        JavascriptExecutorFacade js = new JavascriptExecutorFacade(driver);
        return js.executeAsyncScript(script);
    }

    public Object evaluateJavascript(final String script, final Object... params) {
        addJQuerySupport();
        JavascriptExecutorFacade js = new JavascriptExecutorFacade(driver);
        return js.executeScript(script, params);
    }

    public Object evaluateAsyncJavascript(final String script, final Object... params) {
        addJQuerySupport();
        JavascriptExecutorFacade js = new JavascriptExecutorFacade(driver);
        return js.executeAsyncScript(script, params);
    }

    public void addJQuerySupport() {
        if (pageIsLoaded() && jqueryIntegrationIsActivated() && driverIsJQueryCompatible()) {
            JQueryEnabledPage jQueryEnabledPage = JQueryEnabledPage.withDriver(getDriver());
            jQueryEnabledPage.activateJQuery();
        }
    }

    protected boolean driverIsJQueryCompatible() {
        try {
            if (getDriver() instanceof WebDriverFacade) {
                return SupportedWebDriver.forClass(((WebDriverFacade) getDriver()).getDriverClass())
                        .supportsJavascriptInjection();
            }
            return SupportedWebDriver.forClass(getDriver().getClass()).supportsJavascriptInjection();
        } catch (IllegalArgumentException probablyAMockedDriver) {
            return false;
        }
    }

    private boolean enableJQuery = false;
    public void enableJQuery() { this.enableJQuery = true; }

    private Boolean jqueryIntegrationIsActivated() {
        return enableJQuery || SERENITY_JQUERY_INTEGRATION.booleanFrom(environmentVariables, false);
    }

    public RadioButtonGroup inRadioButtonGroup(String name) {
        return new RadioButtonGroup(getDriver().findElements(By.name(name)));
    }

    private boolean pageIsLoaded() {
        try {
            return (driverIsInstantiated() && getDriver().getCurrentUrl() != null);
        } catch (WebDriverException e) {
            return false;
        }
    }

    protected boolean driverIsInstantiated() {
        if (getDriver() instanceof WebDriverFacade) {
            return ((WebDriverFacade) getDriver()).isEnabled() && ((WebDriverFacade) getDriver()).isInstantiated();
        }
        return true;
    }

    public ThucydidesFluentWait<WebDriver> waitForWithRefresh() {
        return new FluentWaitWithRefresh<>(driver, webdriverClock, sleeper)
                .withTimeout(getWaitForTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .pollingEvery(WAIT_FOR_ELEMENT_PAUSE_LENGTH, TimeUnit.MILLISECONDS)
                .ignoring(NoSuchElementException.class, NoSuchFrameException.class);
    }

    public SerenityFluentWait waitForCondition() {
        return (SerenityFluentWait) new SerenityFluentWait(driver, webdriverClock, sleeper)
                .withTimeout(getWaitForTimeout())
                .pollingEvery(Duration.ofMillis(WAIT_FOR_ELEMENT_PAUSE_LENGTH))
                .ignoring(NoSuchElementException.class, NoSuchFrameException.class);
    }

    public WebElementFacade waitFor(WebElement webElement) {
        return getRenderedView().waitFor(webElement);
    }

    public WebElementFacade waitFor(WebElementFacade webElement) {
        return getRenderedView().waitFor(webElement);
    }

    public WebElementFacadeWait waitForElement() {
        return getRenderedView().waitForElement();
    }

    public Alert getAlert() {
        return driver.switchTo().alert();
    }

    public Actions withAction() {
        WebDriver proxiedDriver = (getDriver() instanceof WebDriverFacade) ?
                ((WebDriverFacade) getDriver()).getProxiedDriver() : getDriver();
        return new SerenityActions(proxiedDriver);
    }

    public class FieldEntry {

        private final CharSequence[] keysToSend;

        public FieldEntry(final CharSequence... keysToSend) {
            this.keysToSend = keysToSend;
        }

        public void into(final WebElement field) {
            element(field).type(keysToSend);
        }

        public void into(final net.serenitybdd.core.pages.WebElementFacade field) {
            field.type(keysToSend);
        }

        public void into(final By bySelector) {
            WebElement field = getDriver().findElement(bySelector);
            into(field);
        }

        public void into(String selector) {
            $(selector).type(keysToSend);
        }
    }

    private void notifyScreenChange() {
        StepEventBus.getEventBus().notifyScreenChange();
    }

    protected ThucydidesFluentAdapter fluent() {
        return new ThucydidesFluentAdapter(getDriver());
    }

    public <T extends WebElementFacade> T moveTo(String xpathOrCssSelector, Object... arguments) {
        if (!driverIsDisabled()) {
            withAction().moveToElement(findBy(xpathOrCssSelector, arguments)).perform();
        }
        return findBy(xpathOrCssSelector, arguments);
    }

    public <T extends WebElementFacade> T moveTo(By locator) {
        if (!driverIsDisabled()) {
            withAction().moveToElement(find(locator)).perform();
        }
        return find(locator);
    }

    public void waitForAngularRequestsToFinish() {
        JavascriptCompatibleVersion.of(getDriver()).ifPresent(
                driver -> WaitForAngular.withDriver(driver).untilAngularRequestsHaveFinished()
        );
    }

    Inflector inflection = Inflector.getInstance();

    @Override
    public String toString() {
        return inflection.of(getClass().getSimpleName())
                .inHumanReadableForm().toString();
    }

    private ListOfWebElementFacades findAllWithRetry(Function<PageObject, ListOfWebElementFacades> finder) {
        return new FindAllWithRetry(environmentVariables).find(finder, this);
    }
}
