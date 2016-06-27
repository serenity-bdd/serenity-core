package net.thucydides.core.webdriver;

import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.fixtureservices.FixtureService;
import net.thucydides.core.util.EnvironmentVariables;
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

    public DesiredCapabilities enhanced(DesiredCapabilities capabilities) {
        CapabilitySet capabilitySet = new CapabilitySet(environmentVariables);
        Map<String, Object> extraCapabilities = capabilitySet.getCapabilities();
        for(String capabilityName : extraCapabilities.keySet()) {
            capabilities.setCapability(capabilityName, extraCapabilities.get(capabilityName));
        }
        addCapabilitiesFromFixtureServicesTo(capabilities);
        return capabilities;
    }

    private void addCapabilitiesFromFixtureServicesTo(DesiredCapabilities capabilities) {
        for(FixtureService fixtureService : fixtureProviderService.getFixtureServices()) {
            fixtureService.addCapabilitiesTo(capabilities);
        }
    }

}
