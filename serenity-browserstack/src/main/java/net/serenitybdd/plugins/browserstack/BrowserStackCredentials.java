package net.serenitybdd.plugins.browserstack;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.Optional;

/**
 * The BrowserStack username and access key will be read from the BROWSERSTACK_USER and BROWSERSTACK_KEY system properties,
 * or (if not defined there) from the browserstack.user and browserstack.key configuration properties
 */
class BrowserStackCredentials {

    private final EnvironmentVariables environmentVariables;

    public BrowserStackCredentials(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static BrowserStackCredentials from(EnvironmentVariables environmentVariables) {
        return new BrowserStackCredentials(environmentVariables);
    }

    public String getUser() {
        String userDefinedInSerenityConf = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("browserstack.user").orElse("");
        Optional<String> browserstackUser = Optional.ofNullable(environmentVariables.getValue("BROWSERSTACK_USER"));
        Optional<String> browserstackUsername = Optional.ofNullable(environmentVariables.getValue("BROWSERSTACK_USERNAME"));
        return browserstackUser.orElse(browserstackUsername.orElse(userDefinedInSerenityConf));
    }

    public String getAccessKey() {
        String keyDefinedInSerenityConf = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("browserstack.key").orElse("");
        Optional<String> browserstackKey = Optional.ofNullable(environmentVariables.getValue("BROWSERSTACK_KEY"));
        Optional<String> browserstackAccessKey = Optional.ofNullable(environmentVariables.getValue("BROWSERSTACK_ACCESS_KEY"));
        return browserstackKey.orElse(browserstackAccessKey.orElse(keyDefinedInSerenityConf));
    }

    public boolean areDefined() {
        return (!getUser().isEmpty() && !getAccessKey().isEmpty());
    }

}
