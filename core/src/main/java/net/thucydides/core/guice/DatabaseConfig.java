package net.thucydides.core.guice;

import java.util.Properties;

/**
 * Provides the configuration properties for the statistics database.
 */
public interface DatabaseConfig {
    Properties getProperties();
    boolean isUsingLocalDatabase();
    void disable();
    void enable();
    boolean isActive();
}
