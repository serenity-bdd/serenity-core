package net.thucydides.core.webdriver;

import net.serenitybdd.core.webdriver.driverproviders.AddCustomDriverCapabilities;
import net.serenitybdd.core.webdriver.driverproviders.AddEnvironmentSpecifiedDriverCapabilities;
import net.serenitybdd.core.webdriver.driverproviders.InsecureCertConfig;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.fixtureservices.FixtureService;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.capabilities.W3CCapabilities;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;



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

    public MutableCapabilities enhanced(MutableCapabilities capabilities, SupportedWebDriver driver) {
        CapabilitySet capabilitySet = new CapabilitySet(environmentVariables);
        addExtraCapabilities(capabilities, capabilitySet);
        if (InsecureCertConfig.acceptInsecureCertsDefinedIn(environmentVariables).orElse(false)) {
            if (capabilities instanceof DesiredCapabilities) {
                ((DesiredCapabilities) capabilities).acceptInsecureCerts();
            }
        }
        addCapabilitiesFromFixtureServicesTo(capabilities);

        AddEnvironmentSpecifiedDriverCapabilities.from(environmentVariables).forDriver(driver).to(capabilities);

        if (StepEventBus.getParallelEventBus() != null && StepEventBus.getParallelEventBus().isBaseStepListenerRegistered()) {
            TestOutcome currentTestOutcome = StepEventBus.getParallelEventBus().getBaseStepListener().getCurrentTestOutcome();
            // Technically not required but needed for some test scenarios
            if ((currentTestOutcome != null)) {
                AddCustomDriverCapabilities.from(environmentVariables)
                                .withTestDetails(driver, currentTestOutcome)
                                .to(capabilities);
            }
        }


        // Add W3C capabilities defined in the "webdriver" section of the serenity.conf file for non-Appium drivers
        if (driver.isW3CCompliant()) {
            capabilities = capabilities.merge(W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities());
        }
        return capabilities;
}

    private void addExtraCapabilities(MutableCapabilities capabilities, CapabilitySet capabilitySet) {
        Map<String, Object> extraCapabilities = capabilitySet.getCapabilities();
        for (String capabilityName : extraCapabilities.keySet()) {
            capabilities.setCapability(capabilityName, extraCapabilities.get(capabilityName));
        }
    }

    private void addCapabilitiesFromFixtureServicesTo(MutableCapabilities capabilities) {
        for (FixtureService fixtureService : fixtureProviderService.getFixtureServices()) {
            fixtureService.addCapabilitiesTo(capabilities);
        }
    }
}
