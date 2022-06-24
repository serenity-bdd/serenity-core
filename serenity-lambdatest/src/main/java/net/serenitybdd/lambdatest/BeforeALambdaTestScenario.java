package net.serenitybdd.lambdatest;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.serenitybdd.core.webdriver.enhancers.ProvidesRemoteWebdriverUrl;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.environment.TestLocalEnvironmentVariables;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.capabilities.CapabilityProperty;
import org.openqa.selenium.MutableCapabilities;

import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;

public class BeforeALambdaTestScenario implements BeforeAWebdriverScenario, ProvidesRemoteWebdriverUrl {

    @Override
    public MutableCapabilities apply(EnvironmentVariables environmentVariables,
                                     SupportedWebDriver driver,
                                     TestOutcome testOutcome,
                                     MutableCapabilities capabilities) {
        if (!LambdaTestConfiguration.isActiveFor(environmentVariables)) {
            return capabilities;
        }

        // Build the webdriver.remote.url if not already defined
        Optional<String> remoteUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL);
        if (!remoteUrl.isPresent()) {
            String lambdatestRemoteUrl = LambdaTestUri.definedIn(environmentVariables).getUri();
            TestLocalEnvironmentVariables.setProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL.getPropertyName(), lambdatestRemoteUrl);
        }
        LambdaTestCredentials lambdaTestCredentials = LambdaTestCredentials.from(environmentVariables);

        HashMap<String, Object> ltOptions = new HashMap<>();

        // Username and access key generally come from the LT_USERNAME and LT_ACCESS_KEY environment variables
        ltOptions.put("user", lambdaTestCredentials.getUser());
        ltOptions.put("accessKey", lambdaTestCredentials.getAccessKey());

        // Define the test name
        ltOptions.put("name", LambdaTestName.from(testOutcome));

        // Fetch LambdaTest-specific properties from the 'lambdatest' section in serenity.conf
        Properties lambdatestProps = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getPropertiesWithPrefixStripped("lambdatest");
        lambdatestProps.forEach((key, value) -> ltOptions.put(key.toString(), CapabilityProperty.asObject(value.toString())));
        capabilities.setCapability("LT:Options", ltOptions);

        return capabilities;
    }

    public boolean isActivated(EnvironmentVariables environmentVariables) {
        return !EnvironmentSpecificConfiguration.from(environmentVariables)
                .getPropertiesWithPrefix("lambdatest").isEmpty();
    }

    @Override
    public Optional<String> remoteUrlDefinedIn(EnvironmentVariables environmentVariables) {
        return Optional.ofNullable(LambdaTestUri.definedIn(environmentVariables).getUri());
    }
}
