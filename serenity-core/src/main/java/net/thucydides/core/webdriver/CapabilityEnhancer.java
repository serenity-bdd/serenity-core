package net.thucydides.core.webdriver;

import net.serenitybdd.core.webdriver.driverproviders.AddCustomDriverCapabilities;
import net.serenitybdd.core.webdriver.driverproviders.AddEnvironmentSpecifiedDriverCapabilities;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.fixtureservices.FixtureService;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;
import java.util.Optional;

import static net.thucydides.core.ThucydidesSystemProperty.ACCEPT_INSECURE_CERTIFICATES;

/**
 * Created by john on 25/06/2016.
 */
public class CapabilityEnhancer {
    private final EnvironmentVariables environmentVariables;
    private final FixtureProviderService fixtureProviderService;

    public CapabilityEnhancer(EnvironmentVariables environmentVariables, FixtureProviderService fixtureProviderService) {
        this.environmentVariables = environmentVariables;
        this.fixtureProviderService = fixtureProviderService;
    }

    public DesiredCapabilities enhanced(DesiredCapabilities capabilities, SupportedWebDriver driver) {
        CapabilitySet capabilitySet = new CapabilitySet(environmentVariables);
        addExtraCapabilities(capabilities, capabilitySet);
        if (ACCEPT_INSECURE_CERTIFICATES.booleanFrom(environmentVariables, false)) {
            capabilities.acceptInsecureCerts();
        }
        addCapabilitiesFromFixtureServicesTo(capabilities);

        AddEnvironmentSpecifiedDriverCapabilities.from(environmentVariables).forDriver(driver).to(capabilities);

        if (StepEventBus.getEventBus() != null && StepEventBus.getEventBus().isBaseStepListenerRegistered()) {
            Optional<TestOutcome> currentTestOutcome = StepEventBus.getEventBus()
                    .getBaseStepListener()
                    .latestTestOutcome();
            // Technically not required but needed for some test scenarios
            if (currentTestOutcome != null) {
                currentTestOutcome.ifPresent(
                        outcome -> AddCustomDriverCapabilities.from(environmentVariables)
                                .withTestDetails(driver, outcome)
                                .to(capabilities)
                );
            }
        }

        return capabilities;
}

    private void addExtraCapabilities(DesiredCapabilities capabilities, CapabilitySet capabilitySet) {
        Map<String, Object> extraCapabilities = capabilitySet.getCapabilities();
        for (String capabilityName : extraCapabilities.keySet()) {
            capabilities.setCapability(capabilityName, extraCapabilities.get(capabilityName));
        }
    }

    private void addCapabilitiesFromFixtureServicesTo(DesiredCapabilities capabilities) {
        for (FixtureService fixtureService : fixtureProviderService.getFixtureServices()) {
            fixtureService.addCapabilitiesTo(capabilities);
        }
    }

}
