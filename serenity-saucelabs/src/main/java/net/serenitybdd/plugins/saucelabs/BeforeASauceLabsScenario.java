package net.serenitybdd.plugins.saucelabs;

import net.serenitybdd.core.environment.CustomDriverConfig;
import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.model.model.TestOutcomeName;
import net.thucydides.core.steps.session.TestSession;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.environment.TestLocalEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.MutableCapabilities;

import java.util.HashMap;
import java.util.Optional;

import static net.serenitybdd.core.environment.CustomDriverConfig.fetchContextFrom;


public class BeforeASauceLabsScenario implements BeforeAWebdriverScenario {

    private final static String SAUCE_OPTIONS = "\"sauce:options\"";
    private final static String SAUCE_CAPABILITY = "sauce:options";

    @Override
    public MutableCapabilities apply(EnvironmentVariables environmentVariables,
                                     SupportedWebDriver driver,
                                     TestOutcome testOutcome,
                                     MutableCapabilities capabilities) {

        if (!SauceLabsConfiguration.isActiveFor(environmentVariables)) {
            return capabilities;
        }
        // Build the webdriver.remote.url if not already defined
        Optional<String> remoteUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL);
        if (!remoteUrl.isPresent()) {
            String sauceRemoteUrl = SauceLabsUri.definedIn(environmentVariables).getUri();
            TestLocalEnvironmentVariables.setProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL.getPropertyName(), sauceRemoteUrl);
        }

        HashMap<String, Object> newOptions = new HashMap<>();

        // Add the test name to the capabilities
        String testName = TestOutcomeName.from(testOutcome);
        if(TestSession.isSessionStarted()) {
            testName = TestSession.getTestSessionContext().getCurrentTestName();
        }

        // Add any other options specified in the webdriver.capabilities.LT:Options section
        CustomDriverConfig.webdriverCapabilitiesConfig(environmentVariables, SAUCE_OPTIONS)
                .ifPresent(options -> options.entrySet()
                                             .forEach(entry -> newOptions.put(entry.getKey(),
                                                                              entry.getValue().unwrapped())));

        // Add the username and access key
        newOptions.put("username", SauceLabsCredentials.from(environmentVariables).getUser());
        newOptions.put("accessKey", SauceLabsCredentials.from(environmentVariables).getAccessKey());

        // Add the test name
        newOptions.put("name", testName);

        // Add the build name
        String context = fetchContextFrom(capabilities, environmentVariables, SAUCE_OPTIONS);
        testOutcome.setContext(context);

        capabilities.setCapability(SAUCE_CAPABILITY, newOptions);
        return capabilities;
    }

    @Override
    public boolean isActivated(EnvironmentVariables environmentVariables) {
        return SauceLabsConfiguration.isActiveFor(environmentVariables);
    }

}
