package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.core.fixtureservices.FixtureProviderService;
import org.openqa.selenium.MutableCapabilities;

public class EnhanceCapabilitiesWithFixtures {

    private final FixtureProviderService fixtureProviderService;

    private EnhanceCapabilitiesWithFixtures(FixtureProviderService fixtureProviderService) {
        this.fixtureProviderService = fixtureProviderService;
    }

    public static EnhanceCapabilitiesWithFixtures using(FixtureProviderService fixtureProviderService) {
        return new EnhanceCapabilitiesWithFixtures(fixtureProviderService);
    }

    public <T extends MutableCapabilities> T into(MutableCapabilities options) {
        applyFixturesTo(options);
        return (T) options;
    }

    private void applyFixturesTo(MutableCapabilities options) {
        fixtureProviderService.getFixtureServices().forEach(
                fixtureService -> fixtureService.addCapabilitiesTo(options)
        );
    }
}
