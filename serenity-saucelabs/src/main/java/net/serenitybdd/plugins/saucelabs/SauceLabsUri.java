package net.serenitybdd.plugins.saucelabs;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The SauceLabs URI can be calculated automatically if the following properties are present:
 *   - SAUCE_USERNAME or browserstack.user
 *   - SAUCE_ACCESS_KEY or browserstack.key
 */
public class SauceLabsUri {
    private final EnvironmentVariables environmentVariables;
    private static final Logger LOGGER = LoggerFactory.getLogger(SauceLabsUri.class);

    private final String username;
    private final String accessKey;

    public SauceLabsUri(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.username = SauceLabsCredentials.from(environmentVariables).getUser();
        this.accessKey = SauceLabsCredentials.from(environmentVariables).getAccessKey();

    }

    public static SauceLabsUri definedIn(EnvironmentVariables environmentVariables) {
        return new SauceLabsUri(environmentVariables);
    }

    private static final String SAUCELABS_HUB = "https://ondemand.#{saucelabs.datacenter}.saucelabs.com/wd/hub";

    public String getUri() {

        String remoteUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getNullableProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL);
        if (remoteUrl != null) {
            return remoteUrl;
        }

        if (username.isEmpty() || accessKey.isEmpty()) {
            LOGGER.warn("It looks like you are trying to connect to BrowserStack, but you haven't defined any credentials. " +
                    "You can set your BrowserStack username and access key either in the BROWSERSTACK_USERNAME and BROWSERSTACK_AUTOMATE_KEY system environment variables, " +
                    "or in the browserstack.username and browserstack.automate.key properties in your serenity.conf file");
            return null;
        }

        return "https://" + username + ":" + accessKey + SAUCELABS_HUB;
    }

}
