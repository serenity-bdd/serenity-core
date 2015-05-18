package net.thucydides.core.fixtureservices;

import org.openqa.selenium.remote.DesiredCapabilities;

public interface FixtureService {
    void setup() throws FixtureException;
    void shutdown() throws FixtureException;
    void addCapabilitiesTo(DesiredCapabilities capabilities);
}
