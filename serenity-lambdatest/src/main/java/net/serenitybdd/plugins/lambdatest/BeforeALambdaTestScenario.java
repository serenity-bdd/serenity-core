package net.serenitybdd.plugins.lambdatest;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.model.TestOutcomeName;
import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.serenitybdd.core.webdriver.enhancers.ProvidesRemoteWebdriverUrl;
import net.serenitybdd.plugins.CapabilityTags;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.environment.TestLocalEnvironmentVariables;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.session.TestSession;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.MutableCapabilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

        HashMap<String, Object> newOptions = new HashMap<>();

        // Username and access key generally come from the LT_USERNAME and LT_ACCESS_KEY environment variables
        newOptions.put("user", lambdaTestCredentials.getUser());
        newOptions.put("accessKey", lambdaTestCredentials.getAccessKey());

        // Define the test name
        String testName = TestOutcomeName.from(testOutcome);
        if(TestSession.isSessionStarted()) {
            testName = TestSession.getTestSessionContext().getCurrentTestName();
        }
        newOptions.put("name", testName);

        // Add tags
        newOptions.put("tags", CapabilityTags.tagsFrom(testOutcome, environmentVariables));

        // The w3c option is set to true by default, unless it is deactivated explicity
        newOptions.put("w3c", true);

        Map<String, Object> currentOptions = (Map<String, Object>) capabilities.getCapability("LT:Options");
        if (currentOptions != null) {
            newOptions.putAll(currentOptions);
        }
        capabilities.setCapability("LT:Options", newOptions);
        return capabilities;
    }

    public boolean isActivated(EnvironmentVariables environmentVariables) {
        return LambdaTestConfiguration.isActiveFor(environmentVariables);
    }

    @Override
    public Optional<String> remoteUrlDefinedIn(EnvironmentVariables environmentVariables) {
        return Optional.ofNullable(LambdaTestUri.definedIn(environmentVariables).getUri());
    }
}
