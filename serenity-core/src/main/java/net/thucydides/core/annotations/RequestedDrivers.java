package net.thucydides.core.annotations;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.DriverStrategySelector;
import net.thucydides.core.webdriver.WebdriverManager;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

class RequestedDrivers {

    private final EnvironmentVariables environmentVariables;
    private WebdriverManager webdriverManager;

    private static Map<String, String> BROWSERSTACK_BROWSER_NAMES = new HashMap();
    private static Map<String, String> SAUCELABS_BROWSER_NAMES = new HashMap();

    static {
        BROWSERSTACK_BROWSER_NAMES.put("iexplorer", "IE");
        BROWSERSTACK_BROWSER_NAMES.put("firefox", "Firefox");
        BROWSERSTACK_BROWSER_NAMES.put("safari", "Safari");
        BROWSERSTACK_BROWSER_NAMES.put("chrome", "Chrome");
        BROWSERSTACK_BROWSER_NAMES.put("opera", "Opera");
        BROWSERSTACK_BROWSER_NAMES.put("edge", "Edge");

        SAUCELABS_BROWSER_NAMES.put("iexplorer", "internet explorer");
        SAUCELABS_BROWSER_NAMES.put("edge", "MicrosoftEdge");

    }

    private RequestedDrivers(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static RequestedDrivers withEnvironmentVariables(EnvironmentVariables environmentVariables) {
        return new RequestedDrivers(environmentVariables);
    }

    public WebDriver requestedDriverFor(String fieldName, String driverName, String driverOptions) {
//        if (DriverStrategySelector.inEnvironment(environmentVariables).shouldUseARemoteDriver()) {
//            return webdriverManager.withOptions(driverOptions)
//                    .withProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_DRIVER.getPropertyName(), driverName)
//                    .getWebdriverByName(fieldName, "remote");
//        } else
        if (DriverStrategySelector.inEnvironment(environmentVariables).browserStackUrlIsDefined()) {
            return webdriverManager.withOptions(driverOptions)
                    .withProperty(ThucydidesSystemProperty.BROWSERSTACK_BROWSER.getPropertyName(),
                            BROWSERSTACK_BROWSER_NAMES.getOrDefault(driverName, driverName))
                    .getWebdriverByName(fieldName, "remote");
        } else if (DriverStrategySelector.inEnvironment(environmentVariables).saucelabsUrlIsDefined()) {
            return webdriverManager.withOptions(driverOptions)
                    .withProperty(ThucydidesSystemProperty.SAUCELABS_BROWSERNAME.getPropertyName(),
                            SAUCELABS_BROWSER_NAMES.getOrDefault(driverName, driverName))
                    .getWebdriverByName(fieldName, "remote");
        } else {
            return webdriverManager.withOptions(driverOptions).getWebdriver(driverName);
        }
    }

    public RequestedDrivers andWebDriverManager(WebdriverManager webdriverManager) {
        this.webdriverManager = webdriverManager;
        return this;
    }
}