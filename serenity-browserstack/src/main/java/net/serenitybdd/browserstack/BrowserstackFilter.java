package net.serenitybdd.browserstack;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.webdriver.RemoteDriver;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.WebDriver;

import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL;

public class BrowserstackFilter {

    public static boolean isDriverEnabled(WebDriver driver) {
        if ((driver == null) || (!RemoteDriver.isARemoteDriver(driver)) || RemoteDriver.isStubbed(driver)) {
            return false;
        }

        if (driver instanceof WebDriverFacade) {
            return ((WebDriverFacade) driver).isInstantiated();
        }

        return true;
    }

    public static  boolean isActivated(EnvironmentVariables environmentVariables) {
        return !EnvironmentSpecificConfiguration.from(environmentVariables)
                .getPropertiesWithPrefix("browserstack").isEmpty();
    }

    public static  boolean isRunningAgainstBrowserstack(EnvironmentVariables environmentVariables) {
        String remoteUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(WEBDRIVER_REMOTE_URL).orElse("");
        return remoteUrl.contains("browserstack");
    }
}
