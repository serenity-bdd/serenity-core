package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.util.EnvironmentVariables;

import static net.thucydides.core.webdriver.WebDriverFactory.getDriverFrom;

public class RemoteDriverName {
    public static String definedIn(EnvironmentVariables environmentVariables) {
        return EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_DRIVER)
                .orElse(
                        EnvironmentSpecificConfiguration.from(environmentVariables)
                                .getOptionalProperty("w3c.browserName")
                                .orElse(
                                        EnvironmentSpecificConfiguration.from(environmentVariables)
                                                .getOptionalProperty("webdriver.browserName")
                                                .orElse(getDriverFrom(environmentVariables))
                                )
                );
    }
}
