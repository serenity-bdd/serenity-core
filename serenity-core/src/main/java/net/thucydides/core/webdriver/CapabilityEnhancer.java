package net.thucydides.core.webdriver;

import net.serenitybdd.core.webdriver.driverproviders.AddCustomDriverCapabilities;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.fixtureservices.FixtureService;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

import static net.thucydides.core.ThucydidesSystemProperty.ACCEPT_INSECURE_CERTIFICATES;
import static net.thucydides.core.webdriver.SupportedWebDriver.IEXPLORER;

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
        addExtraCapabiities(capabilities, capabilitySet);
        if (ACCEPT_INSECURE_CERTIFICATES.booleanFrom(environmentVariables,false)) {
            capabilities.acceptInsecureCerts();
        }
        addCapabilitiesFromFixtureServicesTo(capabilities);

        AddCustomDriverCapabilities.from(environmentVariables).forDriver(driver).to(capabilities);

        return capabilities;
    }

    private void addExtraCapabiities(DesiredCapabilities capabilities, CapabilitySet capabilitySet) {
        Map<String, Object> extraCapabilities = capabilitySet.getCapabilities();
        for(String capabilityName : extraCapabilities.keySet()) {
            capabilities.setCapability(capabilityName, extraCapabilities.get(capabilityName));
        }
    }

    private void addCapabilitiesFromFixtureServicesTo(DesiredCapabilities capabilities) {
        for(FixtureService fixtureService : fixtureProviderService.getFixtureServices()) {
            fixtureService.addCapabilitiesTo(capabilities);
        }
    }

}
