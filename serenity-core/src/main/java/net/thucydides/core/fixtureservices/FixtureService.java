package net.thucydides.core.fixtureservices;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

public interface FixtureService {
    void setup() throws FixtureException;
    void shutdown() throws FixtureException;
    void addCapabilitiesTo(MutableCapabilities capabilities);
}
