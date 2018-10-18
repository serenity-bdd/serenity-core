package net.thucydides.core.fixtureservices;

import org.openqa.selenium.MutableCapabilities;

public interface FixtureService {
    void setup() throws FixtureException;
    void shutdown() throws FixtureException;
    void addCapabilitiesTo(MutableCapabilities capabilities);
}
