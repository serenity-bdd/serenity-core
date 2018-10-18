package net.thucydides.core.webdriver;

import net.serenitybdd.core.webdriver.driverproviders.AddCustomDriverCapabilities;
import net.serenitybdd.core.webdriver.driverproviders.CapabilityValue;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.fixtureservices.FixtureService;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.MutableCapabilities;

import java.util.Map;

import static net.serenitybdd.core.webdriver.driverproviders.CapabilityValue.fromString;
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

    public MutableCapabilities enhanced(MutableCapabilities capabilities, SupportedWebDriver driver) {
        CapabilitySet capabilitySet = new CapabilitySet(environmentVariables);
        addExtraCapabiities(capabilities, capabilitySet);
        addCapabilitiesFromFixtureServicesTo(capabilities);

        AddCustomDriverCapabilities.from(environmentVariables).forDriver(driver).to(capabilities);

        return capabilities;
    }

    private void addExtraCapabiities(MutableCapabilities capabilities, CapabilitySet capabilitySet) {
        Map<String, Object> extraCapabilities = capabilitySet.getCapabilities();
        for(String capabilityName : extraCapabilities.keySet()) {
            capabilities.setCapability(capabilityName, extraCapabilities.get(capabilityName));
        }
    }

    private void addCapabilitiesFromFixtureServicesTo(MutableCapabilities capabilities) {
        for(FixtureService fixtureService : fixtureProviderService.getFixtureServices()) {
            fixtureService.addCapabilitiesTo(capabilities);
        }
    }

}
