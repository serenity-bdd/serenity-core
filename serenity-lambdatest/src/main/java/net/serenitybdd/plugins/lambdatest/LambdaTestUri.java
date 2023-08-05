package net.serenitybdd.plugins.lambdatest;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * The LambdaTest URI can be calculated automatically if the following properties are present:
 *   - LT_USERNAME or lt.user
 *   - LT_ACCESS_KEY or lt.key
 */
public class LambdaTestUri {
    private final EnvironmentVariables environmentVariables;
    private static final Logger LOGGER = LoggerFactory.getLogger(BeforeALambdaTestScenario.class);

    private final String username;
    private final String accessKey;

    public LambdaTestUri(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;

        String ltUserDefinedInSerenityConf = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("lt.user").orElse("");
        String ltKeyDefinedInSerenityConf = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("lt.key").orElse("");

        this.username = Optional.ofNullable(environmentVariables.getValue("LT_USERNAME")).orElse(ltUserDefinedInSerenityConf);
        this.accessKey = Optional.ofNullable(environmentVariables.getValue("LT_ACCESS_KEY")).orElse(ltKeyDefinedInSerenityConf);

    }

    public static LambdaTestUri definedIn(EnvironmentVariables environmentVariables) {
        return new LambdaTestUri(environmentVariables);
    }

    private final static String HUB = "https://%s:%s@%s/wd/hub";
    private final static String SESSION = "https://%s:%s@api.lambdatest.com/automation/api/v1/sessions/%s";

    public String getUri() {

        String remoteUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getNullableProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL);
        if (remoteUrl != null) {
            return remoteUrl;
        }

        if (username.isEmpty() || accessKey.isEmpty()) {
            LOGGER.warn("It looks like you are trying to connect to LambdaTest, but you haven't defined any credentials. " +
                    "You can set your LambdaTest username and access key either in the LT_USERNAME and LT_ACCESS_KEY system environment variables, " +
                    "or in the lt.user and lt.key properties in your serenity.conf file");
            return null;
        }

        String gridUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("lt.grid").orElse("hub.lambdatest.com");
        return "https://" + username + ":" + accessKey + "@" + gridUrl + "/wd/hub";
    }

    public URI getSessionUri(String sessionId) throws URISyntaxException {
        return new URI("https://" + username + ":" + accessKey + "@api.lambdatest.com/automation/api/v1/sessions/" + sessionId);
    }
}
