package net.serenitybdd.plugins.saucelabs;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.Optional;

/**
 * The SauceLabs username and access key will be read from the SAUCE_USERNAME and SAUCE_ACCESS_KEY system properties,
 * or (if not defined there) from the saucelabs.username and saucelabs.accessKey configuration properties
 */
class SauceLabsCredentials {

    private final EnvironmentVariables environmentVariables;

    public SauceLabsCredentials(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static SauceLabsCredentials from(EnvironmentVariables environmentVariables) {
        return new SauceLabsCredentials(environmentVariables);
    }

    public String getUser() {
        String userDefinedInSerenityConf = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("saucelabs.username","sauce.username").orElse("");
        return Optional.ofNullable(environmentVariables.getValue("SAUCE_USERNAME")).orElse(userDefinedInSerenityConf);
    }

    public String getAccessKey() {
        String keyDefinedInSerenityConf = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("saucelabs.accessKey","sauce.key").orElse("");
        return Optional.ofNullable(environmentVariables.getValue("SAUCE_ACCESS_KEY")).orElse(keyDefinedInSerenityConf);
    }

    public boolean areDefined() {
        return (!getUser().isEmpty() && !getAccessKey().isEmpty());
    }

}
