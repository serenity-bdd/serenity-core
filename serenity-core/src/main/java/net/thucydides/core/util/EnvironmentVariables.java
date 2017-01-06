package net.thucydides.core.util;

import java.util.List;
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

    String getProperty(final Enum<?> property);

    String getProperty(final String name, final String defaultValue);

    String getProperty(final Enum<?> property, final String defaultValue);

    void setProperty(final String name, final String value);

    void clearProperty(final String name);

    EnvironmentVariables copy();

    List<String> getKeys();

    Properties getProperties();

    Properties getPropertiesWithPrefix(String prefix);

    boolean aValueIsDefinedFor(final Enum<?> property);

    boolean aValueIsDefinedFor(String property);
}
