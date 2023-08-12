package net.serenitybdd.plugins.bitbar;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.serenitybdd.core.webdriver.enhancers.ProvidesRemoteWebdriverUrl;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.environment.TestLocalEnvironmentVariables;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.MutableCapabilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BeforeABitBarScenario implements BeforeAWebdriverScenario, ProvidesRemoteWebdriverUrl {

    @Override
    public MutableCapabilities apply(
            EnvironmentVariables environmentVariables,
            SupportedWebDriver driver,
            TestOutcome testOutcome,
            MutableCapabilities capabilities) {
        if (!BitBarConfiguration.isActiveFor(environmentVariables)) {
            return capabilities;
        }

        // Build the webdriver.remote.url if not already defined
        Optional<String> remoteUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL);
        if (!remoteUrl.isPresent()) {
            String bitbarRemoteUrl = BitBarUri.definedIn(environmentVariables).getUri();
            TestLocalEnvironmentVariables.setProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL.getPropertyName(), bitbarRemoteUrl);
        }
        HashMap<String, Object> newOptions = new HashMap<>();

        Map<String, Object> currentOptions = (Map<String, Object>) capabilities.getCapability("bitbar:options");
        if (currentOptions != null) {
            newOptions.putAll(currentOptions);
        }
        capabilities.setCapability("bitbar:options", newOptions);
        return capabilities;
    }

    public boolean isActivated(EnvironmentVariables environmentVariables) {
        return BitBarConfiguration.isActiveFor(environmentVariables);
    }

    @Override
    public Optional<String> remoteUrlDefinedIn(EnvironmentVariables environmentVariables) {
        return Optional.ofNullable(BitBarUri.definedIn(environmentVariables).getUri());
    }
}
