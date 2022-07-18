package net.thucydides.core.fixtureservices;

import org.openqa.selenium.MutableCapabilities;

/**
 * You can implement this interface to provide additional capabilities before a driver is instantiated.
 * FixtureService implementations must be declared in a file called net.thucydides.core.fixtureservices.FixtureService
 * in the META-INF/services directory somewhere on the classpath.
 */
public interface FixtureService {
    void setup() throws FixtureException;
    void shutdown() throws FixtureException;
    void addCapabilitiesTo(MutableCapabilities capabilities);
}
