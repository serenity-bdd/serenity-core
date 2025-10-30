package net.serenitybdd.plugins.bitbar;

import net.serenitybdd.core.webdriver.RemoteDriver;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

/**
 * BitBar can be activated by setting the bitbar.active variable to true,
 * or if the "bitbar:options" properties are defined in the webdriver capabilities section of the serenity.conf file.
 */
class BitBarConfiguration {

    public static final String BITBAR_OPTIONS = "\"bitbar:options\"";
    public static final String BITBAR_OPTIONS_PATH = "webdriver.capabilities." + BITBAR_OPTIONS;

    static boolean isActiveFor(EnvironmentVariables environmentVariables) {
        if (EnvironmentSpecificConfiguration.from(environmentVariables).getBooleanProperty("bitbar.active", false)) {
            return true;
        }
        if (!EnvironmentSpecificConfiguration.from(environmentVariables).getPropertiesWithPrefix(BITBAR_OPTIONS_PATH).isEmpty()) {
            return true;
        }
        return EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty("webdriver.remote.url").orElse("")
                .contains("bitbar");
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
