package net.serenitybdd.plugins.saucelabs;

import net.serenitybdd.core.model.TestOutcomeName;
import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.serenitybdd.plugins.CapabilityTags;
import net.serenitybdd.plugins.saucelabs.SaucelabsRemoteDriverCapabilities;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.MutableCapabilities;

import java.util.HashMap;
import java.util.Map;

public class BeforeASauceLabsScenario implements BeforeAWebdriverScenario {
    @Override
    public MutableCapabilities apply(EnvironmentVariables environmentVariables,
                                     SupportedWebDriver driver,
                                     TestOutcome testOutcome,
                                     MutableCapabilities capabilities) {

        if (!SauceLabsConfiguration.isActiveFor(environmentVariables)) {
            return capabilities;
        }

        HashMap<String, Object> newOptions = new HashMap<>();
        SaucelabsRemoteDriverCapabilities sauceCapabilities = new SaucelabsRemoteDriverCapabilities(environmentVariables);

        MutableCapabilities sauceOptions = (MutableCapabilities) sauceCapabilities.getCapabilities(capabilities).getCapability("sauce:options");

        // Add sauce options from environment variables
        sauceOptions.getCapabilityNames()
                .forEach(capabilityName -> newOptions.put(capabilityName, capabilities.getCapability(capabilityName)));

        newOptions.put("username", SauceLabsCredentials.from(environmentVariables).getUser());
        newOptions.put("accessKey", SauceLabsCredentials.from(environmentVariables).getAccessKey());
        newOptions.put("name", TestOutcomeName.from(testOutcome));

        // Add tags
        newOptions.put("tags", CapabilityTags.tagsFrom(testOutcome, environmentVariables));

        if (capabilities.getCapability("sauce:options") != null) {
            if (Map.class.isAssignableFrom(capabilities.getCapability("sauce:options").getClass())) {
                Map<String, Object> currentOptions = (Map<String, Object>) capabilities.getCapability("sauce:options");
                if (currentOptions != null) {
                    newOptions.putAll(currentOptions);
                }
            } else if (MutableCapabilities.class.isAssignableFrom(capabilities.getCapability("sauce:options").getClass())) {
                MutableCapabilities existingSauceOptions = (MutableCapabilities) capabilities.getCapability("sauce:options");
                existingSauceOptions.getCapabilityNames()
                                    .forEach(capabilityName -> newOptions.put(capabilityName,
                                                                              existingSauceOptions.getCapability(capabilityName)));

            }
        }
        capabilities.setCapability("sauce:options", newOptions);
        return capabilities;
    }

    @Override
    public boolean isActivated(EnvironmentVariables environmentVariables) {
        return SauceLabsConfiguration.isActiveFor(environmentVariables);
    }

}
