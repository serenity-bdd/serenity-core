package net.serenitybdd.plugins.bitbar;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The BitBar URI can be calculated automatically if the following properties are present:
 * - BITBAR_API_KEY or bitbar.apiKey
 */
public class BitBarUri {
    private static final Logger LOGGER = LoggerFactory.getLogger(BitBarUri.class);
    private final EnvironmentVariables environmentVariables;
    private final String apiKey;

    public BitBarUri(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.apiKey = BitBarCredentials.from(environmentVariables).getApiKey();

    }

    public static BitBarUri definedIn(EnvironmentVariables environmentVariables) {
        return new BitBarUri(environmentVariables);
    }

    public String getUri() {

        String remoteUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getNullableProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL);
        if (remoteUrl != null) {
            return remoteUrl;
        }

        if (apiKey.isEmpty()) {
            LOGGER.warn("It looks like you are trying to connect to BitBar, but you haven't defined any credentials. " +
                    "You can set your BitBar api key either in the BITBAR_API_KEY system environment variables, " +
                    "or in the bitbar.apiKey properties in your serenity.conf file");
            return null;
        }

        String hub = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("bitbar.hub").orElse("eu-desktop-hub");
        return "https://" + apiKey + "@" + hub + ".bitbar.com/wd/hub";
    }

}
