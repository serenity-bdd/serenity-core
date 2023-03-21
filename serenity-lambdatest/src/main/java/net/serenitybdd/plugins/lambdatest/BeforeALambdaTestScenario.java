package net.serenitybdd.plugins.lambdatest;

import net.serenitybdd.core.environment.CustomDriverConfig;
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
import java.util.Optional;

import static net.serenitybdd.core.environment.CustomDriverConfig.fetchContextFrom;
import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_PROJECT_NAME;

public class BeforeALambdaTestScenario implements BeforeAWebdriverScenario, ProvidesRemoteWebdriverUrl {

    private final static String LT_OPTIONS = "\"LT:Options\"";

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
        newOptions.put("projectName", SERENITY_PROJECT_NAME.from(environmentVariables,"Serenity BDD Test Suite"));
        newOptions.put("w3c", true);

        // Define the test name
        String testName = TestOutcomeName.from(testOutcome);
        if (TestSession.getTestSessionContext().getCurrentTestName() != null) {
            testName = TestSession.getTestSessionContext().getCurrentTestName();
        }
        newOptions.put("name", testName);
        newOptions.put("build", BuildName.from(environmentVariables));

        capabilities.setCapability("name", testName);
        capabilities.setCapability("build", BuildName.from(environmentVariables));
        capabilities.setCapability("projectName", SERENITY_PROJECT_NAME.from(environmentVariables,"Serenity BDD Test Suite"));

        // Add tags
        newOptions.put("tags", CapabilityTags.tagsFrom(testOutcome, environmentVariables));

        // The w3c option is set to true by default, unless it is deactivated explicity
        newOptions.put("w3c", true);

        // Add any other options specified in the webdriver.capabilities.LT:Options section
        CustomDriverConfig.webdriverCapabilitiesConfig(environmentVariables, LT_OPTIONS).ifPresent(ltOptions -> {
            ltOptions.entrySet().forEach(entry -> {
                newOptions.put(entry.getKey(), entry.getValue().unwrapped());
            });
        });

        capabilities.setCapability(LT_OPTIONS, newOptions);

        // Operating system
        // Context from browserName and OS
        String context = fetchContextFrom(capabilities, environmentVariables, LT_OPTIONS);
        testOutcome.setContext(context);


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
