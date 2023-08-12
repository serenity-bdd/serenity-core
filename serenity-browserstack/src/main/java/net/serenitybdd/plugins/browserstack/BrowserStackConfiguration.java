package net.serenitybdd.plugins.browserstack;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.webdriver.RemoteDriver;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.WebDriver;

/**
 * LambdaTest can be activated by setting the browserstack.active variable to true,
 * or if the "bstack:options" properties are defined in the webdriver capabilities section of the serenity.conf file.
 */
class BrowserStackConfiguration {

    public static final String BROWSERSTACK_OPTIONS = "\"bstack:options\"";
    public static final String BROWSERSTACK_OPTIONS_PATH = "webdriver.capabilities." + BROWSERSTACK_OPTIONS;

    static boolean isActiveFor(EnvironmentVariables environmentVariables) {
        if (EnvironmentSpecificConfiguration.from(environmentVariables).getBooleanProperty("browserstack.active", false)) {
            return true;
        }
        if (!EnvironmentSpecificConfiguration.from(environmentVariables).getPropertiesWithPrefix(BROWSERSTACK_OPTIONS_PATH).isEmpty()) {
            return true;
        }
        return EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty("webdriver.remote.url").orElse("")
                .contains("browserstack");
    }

    public static boolean isDriverEnabled(WebDriver driver) {
        if ((driver == null) || (!RemoteDriver.isARemoteDriver(driver)) || RemoteDriver.isStubbed(driver)) {
            return false;
        }

        if (driver instanceof WebDriverFacade) {
            return ((WebDriverFacade) driver).isInstantiated();
        }

        return true;
    }

}
