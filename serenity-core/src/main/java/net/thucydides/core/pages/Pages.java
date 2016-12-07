package net.thucydides.core.pages;

import com.google.common.base.Optional;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.PageObjects;
import net.thucydides.core.annotations.Fields;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebdriverProxyFactory;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * The Pages object keeps track of what web pages a test visits, and helps with mapping pages to Page Objects.
 * A Pages object is associated with a WebDriver driver instance, so you need a Pages object for any
 * given WebDriver driver.
 *
 * @author johnsmart
 */
public class Pages implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient WebDriver driver;

    private static final Logger LOGGER = LoggerFactory.getLogger(Pages.class);

    private String defaultBaseUrl;

    private final Configuration configuration;

    private WebdriverProxyFactory proxyFactory;

    private transient boolean usePreviousPage = false;

    public Pages(Configuration configuration) {
        this.configuration = configuration;
        proxyFactory = WebdriverProxyFactory.getFactory();
    }

    public Pages() {
        this(ConfiguredEnvironment.getConfiguration());
    }

    public Pages(final WebDriver driver) {
        this(ConfiguredEnvironment.getConfiguration());
        this.driver = driver;
    }

    public Pages(final WebDriver driver, Configuration configuration) {
        this(configuration);
        this.driver = driver;
    }

    public void setDriver(final WebDriver driver) {
        this.driver = driver;
    }

    public WebDriver getDriver() {
        return driver;
    }

    protected WebdriverProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    PageObject currentPage = null;

    public <T extends PageObject> T getAt(final Class<T> pageObjectClass) {
        return getPage(pageObjectClass);
    }

    public <T extends PageObject> T getPage(final Class<T> pageObjectClass) {
        T pageCandidate = getCurrentPageOfType(pageObjectClass);
        pageCandidate.setDefaultBaseUrl(getDefaultBaseUrl());
        return pageCandidate;
    }

    @SuppressWarnings("unchecked")
	public <T extends PageObject> T get(final Class<T> pageObjectClass) {
        T nextPage;
        if (shouldUsePreviousPage(pageObjectClass)) {
            nextPage = (T) currentPage;
        } else {
            T pageCandidate = getCurrentPageOfType(pageObjectClass);
            pageCandidate.setDefaultBaseUrl(getDefaultBaseUrl());
            cacheCurrentPage(pageCandidate);
            nextPage = pageCandidate;
        }
        usePreviousPage = false;
        return nextPage;
    }

    @SuppressWarnings("unchecked")
    public <T extends PageObject> T currentPageAt(final Class<T> pageObjectClass) {
        T nextPage;
        if (shouldUsePreviousPage(pageObjectClass)) {
            nextPage = (T) currentPage;
        } else {
            T pageCandidate = getCurrentPageOfType(pageObjectClass);
            pageCandidate.setDefaultBaseUrl(getDefaultBaseUrl());
            openBrowserIfRequiredFor(pageCandidate);
            checkUrlPatterns(pageObjectClass, pageCandidate);
            cacheCurrentPage(pageCandidate);
            nextPage = pageCandidate;
            nextPage.addJQuerySupport();
        }
        usePreviousPage = false;
        return nextPage;
    }

    private <T extends PageObject> void  openBrowserIfRequiredFor(T pageCandidate) {
        if (browserNotOpen()) {
            openHeadlessDriverIfNotOpen();
            pageCandidate.open();
        }
    }


    private void openHeadlessDriverIfNotOpen() {
        if (browserIsHeadless()) {
            driver.get("about:blank");
        }
    }

    private boolean browserNotOpen() {
        if (getDriver() instanceof WebDriverFacade) {
            return !((WebDriverFacade) getDriver()).isInstantiated();
        } else {
            return StringUtils.isEmpty(getDriver().getCurrentUrl());
        }
    }

    private boolean browserIsHeadless() {
        if (getDriver() instanceof WebDriverFacade) {
            return (((WebDriverFacade) getDriver()).getProxiedDriver() instanceof HtmlUnitDriver);
        } else {
            return (getDriver() instanceof HtmlUnitDriver);
        }
    }
    private <T extends PageObject> void checkUrlPatterns(Class<T> pageObjectClass, T pageCandidate) {
        if (!pageCandidate.matchesAnyUrl()) {
            String currentUrl = getDriver().getCurrentUrl();
            if (!pageCandidate.compatibleWithUrl(currentUrl)) {
                thisIsNotThePageYourLookingFor(pageObjectClass);
            }
        }
    }

    private <T extends PageObject> boolean shouldUsePreviousPage(final Class<T> pageObjectClass) {
        if (!usePreviousPage) {
            return false;
        } else {
            return currentPageIsSameTypeAs(pageObjectClass);
        }
    }

    private void cacheCurrentPage(PageObject newPage) {
        this.currentPage = newPage;
    }

    private <T extends PageObject> boolean currentPageIsSameTypeAs(Class<T> pageObjectClass) {
        return (currentPage != null) && (currentPage.getClass().equals(pageObjectClass));
    }

    public boolean isCurrentPageAt(final Class<? extends PageObject> pageObjectClass) {
        try {
            PageObject pageCandidate = getCurrentPageOfType(pageObjectClass);
            String currentUrl = getDriver().getCurrentUrl();
            return pageCandidate.compatibleWithUrl(currentUrl);
        } catch (WrongPageError e) {
            return false;
        }
    }


    /**
     * Create a new Page Object of the given type.
     * The Page Object must have a constructor
     *
     * @param pageObjectClass
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("unchecked")
    private <T extends PageObject> T getCurrentPageOfType(final Class<T> pageObjectClass) {
        T currentPage = null;
        try {
            currentPage = createFromSimpleConstructor(pageObjectClass);
            if (currentPage == null) {
                currentPage = createFromConstructorWithWebdriver(pageObjectClass);
            }
            if (hasPageFactoryProperty(currentPage)) {
                setPageFactory(currentPage);
            }

        } catch (NoSuchMethodException e) {
            LOGGER.warn("This page object does not appear have a constructor that takes a WebDriver parameter: {} ({})",
                    pageObjectClass, e.getMessage());
            thisPageObjectLooksDodgy(pageObjectClass, "This page object does not appear have a constructor that takes a WebDriver parameter");
        } catch (InvocationTargetException e) {
        	// Unwrap the underlying exception
            LOGGER.warn("Failed to instantiate page of type {} ({})", pageObjectClass, e.getTargetException());
            thisPageObjectLooksDodgy(pageObjectClass,"Failed to instantiate page (" + e.getTargetException() +")");
        }catch (Exception e) {
        	//shouldn't even get here
            LOGGER.warn("Failed to instantiate page of type {} ({})", pageObjectClass, e);
            thisPageObjectLooksDodgy(pageObjectClass,"Failed to instantiate page (" + e +")");
        }
        return currentPage;
    }

    private <T extends PageObject> T createFromSimpleConstructor(Class<T> pageObjectClass)
            throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        T newPage = null;
        try {
            Class[] constructorArgs = new Class[0];
            Constructor<? extends PageObject> constructor = pageObjectClass.getConstructor(constructorArgs);
            newPage = (T) constructor.newInstance();
            newPage.setDriver(driver);

        } catch (NoSuchMethodException e) {
            // Try a different constructor
        }
        return newPage;
    }

    private <T extends PageObject> T createFromConstructorWithWebdriver(Class<T> pageObjectClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class[] constructorArgs = new Class[1];
        constructorArgs[0] = WebDriver.class;
        Constructor<? extends PageObject> constructor = pageObjectClass.getConstructor(constructorArgs);
        return (T) constructor.newInstance(driver);
    }

    private boolean hasPageFactoryProperty(Object pageObject) {
        Optional<Field> pagesField = Fields.of(pageObject.getClass()).withName("pages");
        return ((pagesField.isPresent()) && (pagesField.get().getType() == Pages.class));
    }

    private void setPageFactory(Object pageObject) throws IllegalAccessException {
        Optional<Field> pagesField = Fields.of(pageObject.getClass()).withName("pages");
        if (pagesField.isPresent()) {
            pagesField.get().setAccessible(true);
            pagesField.get().set(pageObject, this);
        }
    }


    private void thisPageObjectLooksDodgy(final Class<? extends PageObject> pageObjectClass, String message) {

        String errorDetails = "The page object " + pageObjectClass + " looks dodgy:\n" + message;
        throw new WrongPageError(errorDetails);
    }

    private void thisIsNotThePageYourLookingFor(final Class<? extends PageObject> pageObjectClass) {

        String errorDetails = "This is not the page you're looking for: "
                + "I was looking for a page compatible with " + pageObjectClass + " but "
                + "I was at the URL " + getDriver().getCurrentUrl();

        throw new WrongPageError(errorDetails);
    }

    /**
     * The default URL for this set of tests, or the system default URL if undefined.
     */
    public String getDefaultBaseUrl() {

        String baseUrl = defaultBaseUrl;
        if (isNotEmpty(getConfiguration().getBaseUrl())) {
            baseUrl = getConfiguration().getBaseUrl();
        }
        return baseUrl;
    }

    /**
     * Set a default base URL for a specific set of tests.
     */
    public void setDefaultBaseUrl(final String defaultBaseUrl) {
        this.defaultBaseUrl = defaultBaseUrl;
    }

    public Pages onSamePage() {
        usePreviousPage = true;
        return this;
    }

    public static <T extends PageObject> T instrumentedPageObjectUsing(Class<T> pageObjectClass, WebDriver driver) {
        return PageObjects.usingDriver(driver).ofType(pageObjectClass);
    }
}
