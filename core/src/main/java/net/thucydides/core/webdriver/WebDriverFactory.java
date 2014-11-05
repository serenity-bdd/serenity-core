package net.thucydides.core.webdriver;

import com.google.common.base.Preconditions;
import net.thucydides.core.Thucydides;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.fixtureservices.FixtureException;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.fixtureservices.FixtureService;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.steps.FilePathParser;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.BrowserStackRemoteDriverCapabilities;
import net.thucydides.core.webdriver.capabilities.SauceRemoteDriverCapabilities;
import net.thucydides.core.webdriver.chrome.OptionsSplitter;
import net.thucydides.core.webdriver.firefox.FirefoxProfileEnhancer;
import net.thucydides.core.webdriver.phantomjs.PhantomJSCapabilityEnhancer;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static net.thucydides.core.webdriver.javascript.JavascriptSupport.activateJavascriptSupportFor;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Provides an instance of a supported WebDriver.
 * When you instanciate a Webdriver instance for Firefox or Chrome, it opens a new browser.
 *
 * @author johnsmart
 */
public class WebDriverFactory {
    public static final String DEFAULT_DRIVER = "firefox";
    public static final String REMOTE_DRIVER = "remote";

    private final WebdriverInstanceFactory webdriverInstanceFactory;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverFactory.class);

    private ProfilesIni allProfiles;
    private static final int DEFAULT_HEIGHT = ThucydidesSystemProperty.DEFAULT_HEIGHT;
    private static final int DEFAULT_WIDTH = ThucydidesSystemProperty.DEFAULT_WIDTH;

    private final EnvironmentVariables environmentVariables;
    private final FirefoxProfileEnhancer firefoxProfileEnhancer;
    private final FixtureProviderService fixtureProviderService;
    private final ElementProxyCreator proxyCreator;
    private final BrowserStackRemoteDriverCapabilities browserStackRemoteDriverCapabilities;
    private final SauceRemoteDriverCapabilities sauceRemoteDriverCapabilities;

    private final Integer EXTRA_TIME_TO_TAKE_SCREENSHOTS = 180;

    public WebDriverFactory() {
        this(new WebdriverInstanceFactory(), Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
    }

    public WebDriverFactory(EnvironmentVariables environmentVariables) {
        this(new WebdriverInstanceFactory(), environmentVariables);
    }

    public WebDriverFactory(WebdriverInstanceFactory webdriverInstanceFactory,
                            EnvironmentVariables environmentVariables) {
        this(webdriverInstanceFactory,
                environmentVariables,
                new FirefoxProfileEnhancer(environmentVariables));
    }

    public WebDriverFactory(WebdriverInstanceFactory webdriverInstanceFactory,
                            EnvironmentVariables environmentVariables,
                            FirefoxProfileEnhancer firefoxProfileEnhancer) {
        this(webdriverInstanceFactory, environmentVariables, firefoxProfileEnhancer,
            Injectors.getInjector().getInstance(FixtureProviderService.class),
            Injectors.getInjector().getInstance(ElementProxyCreator.class));
    }

    public WebDriverFactory(WebdriverInstanceFactory webdriverInstanceFactory,
                            EnvironmentVariables environmentVariables,
                            FirefoxProfileEnhancer firefoxProfileEnhancer,
                            FixtureProviderService fixtureProviderService) {
        this(webdriverInstanceFactory, environmentVariables, firefoxProfileEnhancer, fixtureProviderService,
                Injectors.getInjector().getInstance(ElementProxyCreator.class));
    }

    public WebDriverFactory(WebdriverInstanceFactory webdriverInstanceFactory,
                            EnvironmentVariables environmentVariables,
                            FirefoxProfileEnhancer firefoxProfileEnhancer,
                            FixtureProviderService fixtureProviderService,
                            ElementProxyCreator proxyCreator) {
        this.webdriverInstanceFactory = webdriverInstanceFactory;
        this.environmentVariables = environmentVariables;
        this.firefoxProfileEnhancer = firefoxProfileEnhancer;
        this.fixtureProviderService = fixtureProviderService;
        this.proxyCreator = proxyCreator;
        this.browserStackRemoteDriverCapabilities = new BrowserStackRemoteDriverCapabilities(environmentVariables);
        this.sauceRemoteDriverCapabilities = new SauceRemoteDriverCapabilities(environmentVariables);
    }

    protected ProfilesIni getAllProfiles() {
        if (allProfiles == null) {
            allProfiles = new ProfilesIni();
        }
        return allProfiles;
    }

    /**
     * Create a new WebDriver instance of a given type.
     */
    public WebDriver newInstanceOf(final SupportedWebDriver driverType) {
        if (driverType == null) {
            throw new IllegalArgumentException("Driver type cannot be null");
        }
        return newWebdriverInstance(driverType.getWebdriverClass());
    }

    public Class<? extends WebDriver> getClassFor(final SupportedWebDriver driverType) {
        if (usesSauceLabs() && (driverType != SupportedWebDriver.HTMLUNIT)) {
            return RemoteWebDriver.class;
        } else {
            return driverType.getWebdriverClass();
        }
    }

    public boolean usesSauceLabs() {
        return StringUtils.isNotEmpty(sauceRemoteDriverCapabilities.getUrl());
    }
    
    public boolean usesBrowserStack() {
        return StringUtils.isNotEmpty(browserStackRemoteDriverCapabilities.getUrl());
    }
    
    /**
     * This method is synchronized because multiple webdriver instances can be created in parallel.
     * However, they may use common system resources such as ports, so may potentially interfere
     * with each other.
     *
     * @param driverClass
     */
    protected synchronized WebDriver newWebdriverInstance(final Class<? extends WebDriver> driverClass) {
        try {
            WebDriver driver;
            if (isARemoteDriver(driverClass) || shouldUseARemoteDriver() || saucelabsUrlIsDefined() || browserStackUrlIsDefined()) {
            	driver = newRemoteDriver();
            } else if (isAFirefoxDriver(driverClass)) {
                driver = firefoxDriver();
            } else if (isAnHtmlUnitDriver(driverClass)) {
                driver = htmlunitDriver();
            } else if (isAPhantomJSDriver(driverClass)) {
                setPhantomJSPathIfNotSet();
                driver = phantomJSDriver();
            } else if (isAChromeDriver(driverClass)) {
                driver = chromeDriver();
            } else if (isASafariDriver(driverClass)) {
                driver = safariDriver();
            } else if (isAnInternetExplorerDriver(driverClass)) {
                driver = internetExplorerDriver();
            } else if (isAProvidedDriver(driverClass)) {
                driver = providedDriver();
            } else {
                driver = newDriverInstanceFrom(driverClass);
            }
            setImplicitTimeoutsIfSpecified(driver);
            redimensionBrowser(driver);

            activateJavascriptSupportFor(driver);
            return driver;
        } catch (Exception cause) {
            throw new UnsupportedDriverException("Could not instantiate " + driverClass, cause);
        }
    }

    // IntelliJ in Mac OS X does not pick up environment variables. So to get PhantomJS working in IDE mode for the
    // Thucydides tests, add the 'phantomjs.binary.path' property into a thucydides.properties file in your home directory.
    private void setPhantomJSPathIfNotSet() {
        if (!phantomJSIsAvailable()) {
            LOGGER.info("PhantomJS not on path, trying to get path from PHANTOMJS_BINARY_PATH");
            String phantomJSPath = System.getProperty(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY);
            String phantomJSPathEnvironmentProperty = System.getenv("PHANTOMJS_BINARY_PATH");
            LOGGER.info("PHANTOMJS_BINARY_PATH = " + phantomJSPathEnvironmentProperty);
            LOGGER.info("PHANTOMJS_EXECUTABLE_PATH_PROPERTY = " + phantomJSPath);
            if (StringUtils.isNotEmpty(phantomJSPath)) {
                System.setProperty(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomJSPath);
            } else if (StringUtils.isNotEmpty(phantomJSPathEnvironmentProperty)) {
                System.setProperty(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomJSPathEnvironmentProperty);
            }
        }
    }

    private boolean phantomJSIsAvailable() {
        try {
            return (executeCommand("phantomjs -v") != 0);
        } catch (IOException e) {
            return false;
        }
    }

    private int executeCommand(String command) throws IOException {
        Process cmdProc = Runtime.getRuntime().exec(command);
        try {
            return cmdProc.waitFor();
        } catch (InterruptedException e) {
            return -1;
        }
    }

    private boolean isAProvidedDriver(Class<? extends WebDriver> driverClass) {
        return ProvidedDriver.class.isAssignableFrom(driverClass);
    }

    private WebDriver providedDriver() {
        ProvidedDriverConfiguration sourceConfig = new ProvidedDriverConfiguration(environmentVariables);
        try {
            return sourceConfig.getDriverSource().newDriver();
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate the custom webdriver provider of type " + sourceConfig.getDriverName());
        }
    }

    private void setImplicitTimeoutsIfSpecified(WebDriver driver) {
        if (ThucydidesSystemProperty.WEBDRIVER_TIMEOUTS_IMPLICITLYWAIT.isDefinedIn(environmentVariables)) {
            int timeout = environmentVariables.getPropertyAsInteger(ThucydidesSystemProperty.WEBDRIVER_TIMEOUTS_IMPLICITLYWAIT
                                                                                            .getPropertyName(),0);

            driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.MILLISECONDS);
        }
    }

    private boolean shouldUseARemoteDriver() {
        return ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL.isDefinedIn(environmentVariables);
    }

    private WebDriver newDriverInstanceFrom(Class<? extends WebDriver> driverClass) throws IllegalAccessException, InstantiationException {
        return webdriverInstanceFactory.newInstanceOf(driverClass);
    }

    private WebDriver  newRemoteDriver() throws MalformedURLException {

        WebDriver driver = null;
        if (saucelabsUrlIsDefined()) {
            driver = buildSaucelabsDriver();
        } else if (browserStackUrlIsDefined()){
        	driver = buildBrowserStackDriver();
        } else {
            driver = buildRemoteDriver();
        }
        Augmenter augmenter = new Augmenter();
        return augmenter.augment(driver);
    }

    private WebDriver buildRemoteDriver() throws MalformedURLException {
        String remoteUrl = ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL.from(environmentVariables);
        return webdriverInstanceFactory.newRemoteDriver(new URL(remoteUrl), buildRemoteCapabilities());
    }

    private boolean saucelabsUrlIsDefined() {
        return StringUtils.isNotEmpty(sauceRemoteDriverCapabilities.getUrl());
    }

    private boolean browserStackUrlIsDefined() {
        return StringUtils.isNotEmpty(browserStackRemoteDriverCapabilities.getUrl());
    }
    
    private WebDriver buildSaucelabsDriver() throws MalformedURLException {
        String saucelabsUrl = sauceRemoteDriverCapabilities.getUrl();
        WebDriver driver = webdriverInstanceFactory.newRemoteDriver(new URL(saucelabsUrl), findSaucelabsCapabilities());

        if (isNotEmpty(ThucydidesSystemProperty.SAUCELABS_IMPLICIT_TIMEOUT.from(environmentVariables))) {
            int implicitWait = environmentVariables.getPropertyAsInteger(
                    ThucydidesSystemProperty.SAUCELABS_IMPLICIT_TIMEOUT.getPropertyName(), 30);
            driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
        }
        return driver;
    }
    
    private WebDriver buildBrowserStackDriver() throws MalformedURLException{
    	String browserStackUrl = browserStackRemoteDriverCapabilities.getUrl();
        WebDriver driver = webdriverInstanceFactory.newRemoteDriver(new URL(browserStackUrl), findbrowserStackCapabilities());
        return driver;
    }
    

    public static String getDriverFrom(EnvironmentVariables environmentVariables, String defaultDriver) {
        String driver = getDriverFrom(environmentVariables);
        return (driver != null) ? driver : defaultDriver;
    }

    public static String getDriverFrom(EnvironmentVariables environmentVariables) {
        String driver = ThucydidesSystemProperty.WEBDRIVER_DRIVER.from(environmentVariables);
        if (driver == null) {
            driver = ThucydidesSystemProperty.DRIVER.from(environmentVariables);
        }
        return driver;
    }

    private Capabilities findSaucelabsCapabilities() {

        String driver = getDriverFrom(environmentVariables);
        DesiredCapabilities capabilities = capabilitiesForDriver(driver);

        return sauceRemoteDriverCapabilities.getCapabilities(capabilities);
    }
    
    
    private Capabilities findbrowserStackCapabilities() {

    	String driver = getDriverFrom(environmentVariables);
    	DesiredCapabilities capabilities = capabilitiesForDriver(driver);
    	
        return browserStackRemoteDriverCapabilities.getCapabilities(capabilities);

    }

    private Capabilities buildRemoteCapabilities() {
        String driver = ThucydidesSystemProperty.WEBDRIVER_REMOTE_DRIVER.from(environmentVariables);
        if (driver == null) {
            driver = getDriverFrom(environmentVariables);
        }
        return capabilitiesForDriver(driver);
    }


    private DesiredCapabilities capabilitiesForDriver(String driver) {
        if (driver == null) {
            driver = REMOTE_DRIVER;
        }
        SupportedWebDriver driverType = driverTypeFor(driver);

        Preconditions.checkNotNull(driverType, "Unsupported remote driver type: ");

        if (driverType == SupportedWebDriver.REMOTE) {
            return (DesiredCapabilities) enhancedCapabilities(remoteCapabilities());
        } else {
            return (DesiredCapabilities) enhancedCapabilities(realBrowserCapabilities(driverType));
        }
    }

    private SupportedWebDriver driverTypeFor(String driver) {
        String normalizedDriverName = driver.toUpperCase();
        if (!SupportedWebDriver.listOfSupportedDrivers().contains(normalizedDriverName)) {
            SupportedWebDriver closestDriver = SupportedWebDriver.getClosestDriverValueTo(normalizedDriverName);
            throw new AssertionError("Unsupported driver for webdriver.driver or webdriver.remote.driver: " + driver
                                     + ". Did you mean " + closestDriver.toString().toLowerCase() + "?");
        }
        return SupportedWebDriver.valueOf(normalizedDriverName);
    }

    private DesiredCapabilities realBrowserCapabilities(SupportedWebDriver driverType) {

        DesiredCapabilities capabilities = null;

        switch (driverType) {
            case CHROME:
                capabilities = chromeCapabilities();
                break;

            case SAFARI:
                capabilities = DesiredCapabilities.safari();
                break;

            case FIREFOX:
                capabilities = DesiredCapabilities.firefox();
                capabilities.setCapability("firefox_profile",buildFirefoxProfile());
                break;

            case HTMLUNIT:
                capabilities = DesiredCapabilities.htmlUnit();
                break;

            case OPERA:
                capabilities = DesiredCapabilities.opera();
                break;

            case IEXPLORER:
                capabilities = DesiredCapabilities.internetExplorer();
                break;

            default:
                capabilities = new DesiredCapabilities();
                capabilities.setJavascriptEnabled(true);
        }
        return (DesiredCapabilities) enhancedCapabilities(capabilities);
    }

    private DesiredCapabilities remoteCapabilities() {
        String remoteBrowser = ThucydidesSystemProperty.WEBDRIVER_REMOTE_DRIVER.from(environmentVariables, "firefox");
        DesiredCapabilities capabilities = realBrowserCapabilities(driverTypeFor(remoteBrowser));
        capabilities.setCapability("idle-timeout",EXTRA_TIME_TO_TAKE_SCREENSHOTS);

        Boolean recordScreenshotsInSaucelabs
              = environmentVariables.getPropertyAsBoolean(ThucydidesSystemProperty.SAUCELABS_RECORD_SCREENSHOTS, false);
        capabilities.setCapability("record-screenshots", recordScreenshotsInSaucelabs);


        if (environmentVariables.getProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_OS) != null) {
            capabilities.setCapability("platform", Platform.valueOf(environmentVariables.getProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_OS)));
        }

        if (environmentVariables.getProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_BROWSER_VERSION) != null) {
            capabilities.setCapability("version", Platform.valueOf(environmentVariables.getProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_BROWSER_VERSION)));
        }

        return capabilities;
    }

    private DesiredCapabilities addExtraCatabilitiesTo(DesiredCapabilities capabilities) {
        CapabilitySet capabilitySet = new CapabilitySet(environmentVariables);
        Map<String, Object> extraCapabilities = capabilitySet.getCapabilities();
        for(String capabilityName : extraCapabilities.keySet()) {
            capabilities.setCapability(capabilityName, extraCapabilities.get(capabilityName));
        }
        addCapabilitiesFromFixtureServicesTo(capabilities);
        return capabilities;
    }

    public void setupFixtureServices() throws FixtureException {
        for(FixtureService fixtureService : fixtureProviderService.getFixtureServices()) {
            fixtureService.setup();
        }
    }

    public void shutdownFixtureServices() {
        for(FixtureService fixtureService : fixtureProviderService.getFixtureServices()) {
            fixtureService.shutdown();
        }
    }

    private void addCapabilitiesFromFixtureServicesTo(DesiredCapabilities capabilities) {
        for(FixtureService fixtureService : fixtureProviderService.getFixtureServices()) {
            fixtureService.addCapabilitiesTo(capabilities);
        }
    }

    private WebDriver htmlunitDriver() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        DesiredCapabilities capabilities = DesiredCapabilities.htmlUnit();
        capabilities.setJavascriptEnabled(true);
        return webdriverInstanceFactory.newHtmlUnitDriver(enhancedCapabilities(capabilities));
    }

    private WebDriver phantomJSDriver() {
        DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
        PhantomJSCapabilityEnhancer enhancer = new PhantomJSCapabilityEnhancer(environmentVariables);
        enhancer.enhanceCapabilities(capabilities);
        return webdriverInstanceFactory.newPhantomDriver(enhancedCapabilities(capabilities));
    }

    private WebDriver firefoxDriver() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        FirefoxProfile profile = buildFirefoxProfile();
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability(FirefoxDriver.PROFILE, profile);
        return webdriverInstanceFactory.newFirefoxDriver(enhancedCapabilities(capabilities));
    }

    private WebDriver chromeDriver() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        DesiredCapabilities capabilities = chromeCapabilities();
        updateChromePathIfSpecifiedIn(environmentVariables);
        return webdriverInstanceFactory.newChromeDriver(enhancedCapabilities(capabilities));

    }

    private void updateChromePathIfSpecifiedIn(EnvironmentVariables environmentVariables) {
        String environmentDefinedChromeDriverPath = environmentVariables.getProperty(ThucydidesSystemProperty.WEBDRIVER_CHROME_DRIVER);
        if (StringUtils.isNotEmpty(environmentDefinedChromeDriverPath)) {
            System.setProperty(ThucydidesSystemProperty.WEBDRIVER_CHROME_DRIVER.toString(), environmentDefinedChromeDriverPath);
        }
    }

    private DesiredCapabilities chromeCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        String chromeSwitches = environmentVariables.getProperty(ThucydidesSystemProperty.CHROME_SWITCHES);
        capabilities.setCapability(ChromeOptions.CAPABILITY, optionsFromSwitches(chromeSwitches));
        capabilities.setCapability("chrome.switches", chromeSwitches);
        return capabilities;
    }

    private ChromeOptions optionsFromSwitches(String chromeSwitches) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("test-type");
        if (StringUtils.isNotEmpty(chromeSwitches)) {
            List<String> arguments = new OptionsSplitter().split(chromeSwitches);
            options.addArguments(arguments);
        }
        return options;
    }

    private WebDriver safariDriver() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return webdriverInstanceFactory.newSafariDriver(enhancedCapabilities(DesiredCapabilities.safari()));

    }

    private WebDriver internetExplorerDriver() {
        return webdriverInstanceFactory.newInternetExplorerDriver(enhancedCapabilities(DesiredCapabilities.internetExplorer()));
    }

    private Capabilities enhancedCapabilities(DesiredCapabilities capabilities) {
        return addExtraCatabilitiesTo(capabilities);
    }

    private Dimension getRequestedBrowserSize() {
        int height = environmentVariables.getPropertyAsInteger(ThucydidesSystemProperty.THUCYDIDES_BROWSER_HEIGHT, DEFAULT_HEIGHT);
        int width = environmentVariables.getPropertyAsInteger(ThucydidesSystemProperty.THUCYDIDES_BROWSER_WIDTH, DEFAULT_WIDTH);
        return new Dimension(width, height);
    }

    private void redimensionBrowser(final WebDriver driver) {
        if (supportsScreenResizing(driver) && browserDimensionsSpecified()) {
            resizeBrowserTo(driver,
                    getRequestedBrowserSize().height,
                    getRequestedBrowserSize().width);
        }
    }

    private boolean browserDimensionsSpecified() {
        String snapshotWidth = environmentVariables.getProperty(ThucydidesSystemProperty.THUCYDIDES_BROWSER_WIDTH);
        String snapshotHeight = environmentVariables.getProperty(ThucydidesSystemProperty.THUCYDIDES_BROWSER_HEIGHT);
        return (snapshotWidth != null) || (snapshotHeight != null);
    }

    private boolean supportsScreenResizing(final WebDriver driver) {
        return isNotAMocked(driver) && (!isAnHtmlUnitDriver(getDriverClass(driver)));
    }

    private boolean isNotAMocked(WebDriver driver) {
        return (!driver.getClass().getName().contains("Mock"));
    }

    protected void resizeBrowserTo(WebDriver driver, int height, int width) {

        LOGGER.info("Setting browser dimensions to {}/{}", height, width);

        if (usesFirefox(driver) || usesInternetExplorer(driver) || usesPhantomJS(driver)) {
            driver.manage().window().setSize(new Dimension(width, height));
        } else if (usesChrome(driver)) {
            ((JavascriptExecutor) driver).executeScript("window.open('about:blank','_blank','width=#{width},height=#{height}');");
            Set<String> windowHandles = driver.getWindowHandles();
            windowHandles.remove(driver.getWindowHandle());
            String newWindowHandle = windowHandles.toArray(new String[]{})[0];
            driver.switchTo().window(newWindowHandle);
        }
        String resizeWindow = "window.resizeTo(" + width + "," + height + ")";
        ((JavascriptExecutor) driver).executeScript(resizeWindow);
    }

    private boolean isARemoteDriver(Class<? extends WebDriver> driverClass) {
        return (RemoteWebDriver.class == driverClass);
    }

    private boolean isAFirefoxDriver(Class<? extends WebDriver> driverClass) {
        return (FirefoxDriver.class.isAssignableFrom(driverClass));
    }

    private boolean isAChromeDriver(Class<? extends WebDriver> driverClass) {
        return (ChromeDriver.class.isAssignableFrom(driverClass));
    }

    private boolean isASafariDriver(Class<? extends WebDriver> driverClass) {
        return (SafariDriver.class.isAssignableFrom(driverClass));
    }

    private boolean isAnInternetExplorerDriver(Class<? extends WebDriver> driverClass) {
        return (InternetExplorerDriver.class.isAssignableFrom(driverClass));
    }

    private boolean usesFirefox(WebDriver driver) {
        return (FirefoxDriver.class.isAssignableFrom(getDriverClass(driver)));
    }

    private boolean usesInternetExplorer(WebDriver driver) {
        return (InternetExplorerDriver.class.isAssignableFrom(getDriverClass(driver)));
    }

    private boolean usesChrome(WebDriver driver) {
        return (ChromeDriver.class.isAssignableFrom(getDriverClass(driver)));
    }

    private Class getDriverClass(WebDriver driver) {
        Class driverClass = null;
        if (driver instanceof WebDriverFacade) {
            driverClass = ((WebDriverFacade) driver).getDriverClass();
        } else {
            driverClass = driver.getClass();
        }
        return driverClass;
    }

    private boolean isAnHtmlUnitDriver(Class<? extends WebDriver> driverClass) {
        return (HtmlUnitDriver.class.isAssignableFrom(driverClass));
    }

    private boolean isAPhantomJSDriver(Class<? extends WebDriver> driverClass) {
        return (PhantomJSDriver.class.isAssignableFrom(driverClass));
    }
    private boolean usesPhantomJS(WebDriver driver) {
        return (PhantomJSDriver.class.isAssignableFrom(getDriverClass(driver)));
    }


    protected FirefoxProfile createNewFirefoxProfile() {
        FirefoxProfile profile;
        if (Thucydides.getFirefoxProfile() != null) {
            profile = Thucydides.getFirefoxProfile();
        } else {
            profile = new FirefoxProfile();
            profile.setPreference("network.proxy.socks_port",9999);
            profile.setAlwaysLoadNoFocusLib(true);
            profile.setEnableNativeEvents(true);
        }
        return profile;
    }

    protected FirefoxProfile useExistingFirefoxProfile(final File profileDirectory) {
        return new FirefoxProfile(profileDirectory);
    }

    protected FirefoxProfile buildFirefoxProfile() {
        String profileName = ThucydidesSystemProperty.WEBDRIVER_FIREFOX_PROFILE.from(environmentVariables);
        FilePathParser parser = new FilePathParser(environmentVariables);
        DesiredCapabilities firefoxCapabilities = DesiredCapabilities.firefox();
        if (StringUtils.isNotEmpty(profileName)) {
            firefoxCapabilities.setCapability(FirefoxDriver.PROFILE, parser.getInstanciatedPath(profileName));
        }


        FirefoxProfile profile;
        if (profileName == null) {
            profile = createNewFirefoxProfile();
        } else {
            profile = getProfileFrom(profileName);
        }

        firefoxProfileEnhancer.allowWindowResizeFor(profile);
        firefoxProfileEnhancer.activateNativeEventsFor(profile, shouldEnableNativeEvents());
        if (shouldActivateProxy()) {
            activateProxyFor(profile, firefoxProfileEnhancer);
        }
        if (firefoxProfileEnhancer.shouldActivateFirebugs()) {
            firefoxProfileEnhancer.addFirebugsTo(profile);
        }
        if (refuseUntrustedCertificates()) {
            profile.setAssumeUntrustedCertificateIssuer(false);
            profile.setAcceptUntrustedCertificates(false);
        } else {
            profile.setAssumeUntrustedCertificateIssuer(true);
            profile.setAcceptUntrustedCertificates(true);
        }
        firefoxProfileEnhancer.configureJavaSupport(profile);
        firefoxProfileEnhancer.addPreferences(profile);
        return profile;
    }

    private boolean shouldEnableNativeEvents() {
        return Boolean.valueOf(ThucydidesSystemProperty.THUCYDIDES_NATIVE_EVENTS.from(environmentVariables,"true"));
    }

    private void activateProxyFor(FirefoxProfile profile, FirefoxProfileEnhancer firefoxProfileEnhancer) {
        String proxyUrl = getProxyUrlFromEnvironmentVariables();
        String proxyPort = getProxyPortFromEnvironmentVariables();
        firefoxProfileEnhancer.activateProxy(profile, proxyUrl, proxyPort);
    }

    private String getProxyPortFromEnvironmentVariables() {
        return environmentVariables.getProperty(ThucydidesSystemProperty.THUCYDIDES_PROXY_HTTP_PORT.getPropertyName());
    }

    private boolean shouldActivateProxy() {
        String proxyUrl = getProxyUrlFromEnvironmentVariables();
        return StringUtils.isNotEmpty(proxyUrl);
    }

    private String getProxyUrlFromEnvironmentVariables() {
        return environmentVariables.getProperty(ThucydidesSystemProperty.THUCYDIDES_PROXY_HTTP.getPropertyName());
    }

    private FirefoxProfile getProfileFrom(final String profileName) {
        FirefoxProfile profile = getAllProfiles().getProfile(profileName);
        if (profile == null) {
            profile = useExistingFirefoxProfile(new File(profileName));
        }
        return profile;
    }

    private boolean refuseUntrustedCertificates() {
        return environmentVariables.getPropertyAsBoolean(ThucydidesSystemProperty.REFUSE_UNTRUSTED_CERTIFICATES.getPropertyName(), false);
    }

    /**
     * Initialize a page object's fields using the specified WebDriver instance.
     */
    public void initElementsWithAjaxSupport(final PageObject pageObject, final WebDriver driver, int timeoutInSeconds) {
    	proxyCreator.proxyElements(pageObject, driver, timeoutInSeconds);
    }

}
