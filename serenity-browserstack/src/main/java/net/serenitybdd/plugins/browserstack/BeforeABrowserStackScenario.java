package net.serenitybdd.plugins.browserstack;

import net.serenitybdd.core.environment.CustomDriverConfig;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.model.model.TestOutcomeName;
import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.serenitybdd.core.webdriver.enhancers.ProvidesRemoteWebdriverUrl;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.environment.TestLocalEnvironmentVariables;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.core.steps.session.TestSession;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.MutableCapabilities;

import java.util.HashMap;
import java.util.Optional;

import static net.serenitybdd.core.environment.CustomDriverConfig.fetchContextFrom;

public class BeforeABrowserStackScenario implements BeforeAWebdriverScenario, ProvidesRemoteWebdriverUrl {

    private final static String BSTACK_OPTIONS_CONFIG_PROPERTY = "\"bstack:options\"";
    private final static String BSTACK_OPTIONS_CAPABILITY = "bstack:options";

    @Override
    public MutableCapabilities apply(EnvironmentVariables environmentVariables,
                                     SupportedWebDriver driver,
                                     TestOutcome testOutcome,
                                     MutableCapabilities capabilities) {
        if (!BrowserStackConfiguration.isActiveFor(environmentVariables)) {
            return capabilities;
        }

        // Build the webdriver.remote.url if not already defined
        Optional<String> remoteUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL);
        if (!remoteUrl.isPresent()) {
            String browserstackRemoteUrl = BrowserStackUri.definedIn(environmentVariables).getUri();
            TestLocalEnvironmentVariables.setProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL.getPropertyName(), browserstackRemoteUrl);
        }
        HashMap<String, Object> newOptions = new HashMap<>();

        // Add the test name to the capabilities
        String testName = TestOutcomeName.from(testOutcome);
        if(TestSession.isSessionStarted()) {
            testName = TestSession.getTestSessionContext().getCurrentTestName();
        }

        // Add any other options specified in the webdriver.capabilities.LT:Options section
        CustomDriverConfig.webdriverCapabilitiesConfig(environmentVariables, BSTACK_OPTIONS_CONFIG_PROPERTY).ifPresent(ltOptions -> {
            ltOptions.entrySet().forEach(entry -> {
                newOptions.put(entry.getKey(), entry.getValue().unwrapped());
            });
        });

        // Add the test name to the capabilities
        newOptions.put("sessionName", testName);

        // Add the build name to the capabilities
        newOptions.put("buildName", BuildNameGenerator.forEnvironmentVariables(environmentVariables).getBuildName());


        // Add the Browserstack options to the capabilities
        capabilities.setCapability(BSTACK_OPTIONS_CAPABILITY, newOptions);

        String context = fetchContextFrom(capabilities, environmentVariables, BSTACK_OPTIONS_CONFIG_PROPERTY);
        testOutcome.setContext(context);

        return capabilities;
    }

    public boolean isActivated(EnvironmentVariables environmentVariables) {
        return BrowserStackConfiguration.isActiveFor(environmentVariables);
    }

    @Override
    public Optional<String> remoteUrlDefinedIn(EnvironmentVariables environmentVariables) {
        return Optional.ofNullable(BrowserStackUri.definedIn(environmentVariables).getUri());
    }
}
