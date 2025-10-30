package net.serenitybdd.plugins.saucelabs;

import net.serenitybdd.core.webdriver.RemoteDriver;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

/**
 * LambdaTest can be activated by setting the sauce.active variable to true,
 * or if the "sauce:options" properties are defined in the webdriver capabilities section of the serenity.conf file.
 */
class SauceLabsConfiguration {

    public static final String SAUCELABS_OPTIONS = "\"sauce:options\"";
    public static final String SAUCELABS_OPTIONS_PATH = "webdriver.capabilities." + SAUCELABS_OPTIONS;

    static boolean isActiveFor(EnvironmentVariables environmentVariables) {
        if (EnvironmentSpecificConfiguration.from(environmentVariables).getBooleanProperty("sauce.active", false)) {
            return true;
        }
        if (!EnvironmentSpecificConfiguration.from(environmentVariables).getPropertiesWithPrefix(SAUCELABS_OPTIONS_PATH).isEmpty()) {
            return true;
        }
        return EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty("webdriver.remote.url").orElse("")
                .contains("saucelabs");
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
