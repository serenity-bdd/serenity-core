package net.serenitybdd.plugins.browserstack;

import com.typesafe.config.Config;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.model.TestOutcomeName;
import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.serenitybdd.core.webdriver.enhancers.ProvidesRemoteWebdriverUrl;
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

public class BeforeABrowserStackScenario implements BeforeAWebdriverScenario, ProvidesRemoteWebdriverUrl {

    private final static String BSTACK_OPTIONS = "\"bstack:options\"";

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

        // Username and access key generally come from the LT_USERNAME and LT_ACCESS_KEY environment variables

        String testName = TestOutcomeName.from(testOutcome);
        if(TestSession.isSessionStarted()) {
            testName = TestSession.getTestSessionContext().getCurrentTestName();
        }

        Config specifiedOptions = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getConfig("webdriver.capabilities")
                .getConfig(BSTACK_OPTIONS);

        specifiedOptions.entrySet().forEach(entry -> {
            newOptions.put(entry.getKey(), entry.getValue().unwrapped());
        });

        // Add the test name to the capabilities
        newOptions.put("sessionName", testName);

        // Add the Browserstack options to the capabilities
        capabilities.setCapability(BSTACK_OPTIONS, newOptions);

        // Browser name
        String browserName = capabilities.getBrowserName();
        // Operating system
        String os = specifiedOptions.getString("platformName");
        // Context from browserName and OS
        String context = (os != null) ? browserName + ", " + os : browserName;

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
