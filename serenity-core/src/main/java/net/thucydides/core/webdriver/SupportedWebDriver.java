package net.thucydides.core.webdriver;

import io.appium.java_client.AppiumDriver;
import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.core.strings.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The list of supported web drivers.
 * These are the drivers that support screenshots.
 */
public enum SupportedWebDriver {
    /**
     * Firefox WebDriver driver.
     */
    FIREFOX(FirefoxDriver.class),

    /**
     * Chrome  WebDriver driver.
     */
    CHROME(ChromeDriver.class),

    /**
     * Opera - use it with SauceLabs
     */
    OPERA(OperaDriver.class),

    /**
     * HTMLUnit - fast, but no screenshots.
     */
    HTMLUNIT(HtmlUnitDriver.class),

    /**
     * Phantom-JS driver - headless javascript.
     */
    PHANTOMJS(PhantomJSDriver.class),
    /**
     * Remote web driver
     */
    REMOTE(RemoteWebDriver.class),

    IPHONE(RemoteWebDriver.class),

    ANDROID(RemoteWebDriver.class),

    /**
     * Internet Explorer
     */
    IEXPLORER(InternetExplorerDriver.class, false, NewList.of("IE")),

    /**
     * Microsoft Edge
     */
    EDGE(EdgeDriver.class, false),

    /**
     * Safari
     */
    SAFARI(SafariDriver.class),

    /**
     * Appium
     */
    APPIUM(AppiumDriver.class),

    /**
     * A user-provided driver
     */
    PROVIDED(ProvidedDriver.class);

    private final Class<? extends WebDriver> webdriverClass;

    private final boolean supportsJavascriptInjection;

    private final List<String> synonyms;

    SupportedWebDriver(Class<? extends WebDriver> webdriverClass,
                               boolean supportsJavascriptInjection,
                               List<String> synonyms) {
        this.webdriverClass = webdriverClass;
        this.supportsJavascriptInjection = supportsJavascriptInjection;
        this.synonyms = NewList.copyOf(synonyms);
    }

    SupportedWebDriver(Class<? extends WebDriver> webdriverClass, boolean supportsJavascriptInjection) {
        this(webdriverClass, supportsJavascriptInjection, new ArrayList<String>());
    }

    SupportedWebDriver(Class<? extends WebDriver> webdriverClass) {
        this(webdriverClass, true, new ArrayList<String>());
    }

    public static SupportedWebDriver valueOrSynonymOf(String driverName) {
        for(SupportedWebDriver supportedWebDriver : values()) {
            if (driverName.trim().equalsIgnoreCase(supportedWebDriver.name())) {
                return supportedWebDriver;
            }
            if (supportedWebDriver.synonyms.contains(driverName.trim())) {
                return supportedWebDriver;
            }
        }
        throw new IllegalArgumentException("Unsupported driver type: " + driverName);
    }

    public Class<? extends WebDriver> getWebdriverClass() {
        return webdriverClass;
    }

    public static String listOfSupportedDrivers() {

        String enumValues = Joiner.on(", ").join(Arrays.stream(SupportedWebDriver.values()).map(Enum::toString).collect(Collectors.toList()));

        return Joiner.on(", ").join(getSynonymes(), enumValues);
    }

    private static String getSynonymes() {
        List<String> synonymeValues = new ArrayList<>();
        for(SupportedWebDriver supportedWebDriver : values()) {
            synonymeValues.addAll(supportedWebDriver.synonyms);
        }
        return Joiner.on(", ").join(synonymeValues);
    }

    public static SupportedWebDriver getClosestDriverValueTo(final String value) {
        SupportedWebDriver closestDriver = null;
        int closestDriverDistance = Integer.MAX_VALUE;
        for (SupportedWebDriver supportedDriver : values()) {
            int distance = StringUtils.getLevenshteinDistance(supportedDriver.toString(), value);
            if (distance < closestDriverDistance) {
                closestDriverDistance = distance;
                closestDriver = supportedDriver;
            }
        }
        return closestDriver;
    }

    public static SupportedWebDriver getDriverTypeFor(final String value) throws DriverConfigurationError {
        try {
            return SupportedWebDriver.valueOrSynonymOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            SupportedWebDriver closestMatchingDriver = getClosestDriverValueTo(value);
            throw new DriverConfigurationError("Unsupported browser type: " + value
                    + ". Did you mean " + closestMatchingDriver.toString().toLowerCase()
                    + "?", e);
        }

    }

    public static SupportedWebDriver forClass(Class<?> driverClass) {
        for (SupportedWebDriver supportedWebDriver : values()) {
            if (driverClass.isAssignableFrom(supportedWebDriver.getWebdriverClass())) {
                return supportedWebDriver;
            }
        }

        throw new IllegalArgumentException("Driver not supported: " + driverClass);
    }

    public boolean supportsJavascriptInjection() {
        return supportsJavascriptInjection;
    }
}