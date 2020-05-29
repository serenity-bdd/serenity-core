package net.thucydides.core.util;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Return system environment variable values.
 */
public interface EnvironmentVariables {

    String getValue(final String name);

    String getValue(final Enum<?> property);

    String getValue(final String name, final String defaultValue);

    String getValue(Enum<?> property, String defaultValue);

    Integer getPropertyAsInteger(final String name, final Integer defaultValue);

    Integer getPropertyAsInteger(final Enum<?> property, final Integer defaultValue);

    Boolean getPropertyAsBoolean(final String name, boolean defaultValue);

    Boolean getPropertyAsBoolean(final Enum<?> property, boolean defaultValue);

    String getProperty(final String name);

    /**
     * Returns an optional system property.
     * The property may be defined in the project's serenity.properties or serenity.conf file, or be provided as a
     * a system property. This is designed particularly for user-provided properties, to make it easier to store
     * test confiuration properties in a single file.
     *
     * Sample usage:
     * ```
     * EnvironmentVariables environmentVariables;
     *
     * String environment = environmentVariables.optionalProperty("env").orElse("DEV")
     * ```
     */
    Optional<String> optionalProperty(String name);

    String getProperty(final Enum<?> property);

    String getProperty(final String name, final String defaultValue);

    String getProperty(final Enum<?> property, final String defaultValue);

    void setProperty(final String name, final String value);

    void setProperties(Map<String, String> properties);

    void clearProperty(final String name);

    EnvironmentVariables copy();

    List<String> getKeys();

    Properties getProperties();

    Properties getPropertiesWithPrefix(String prefix);

    boolean aValueIsDefinedFor(final Enum<?> property);

    boolean aValueIsDefinedFor(String property);

    String injectSystemPropertiesInto(String value);

    Map<String, String> asMap();

    Map<String,String> simpleSystemPropertiesAsMap();
}
