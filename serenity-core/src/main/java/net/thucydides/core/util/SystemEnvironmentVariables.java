package net.thucydides.core.util;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.DefaultStringConverter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Return system environment variable values.
 */
public class SystemEnvironmentVariables implements EnvironmentVariables {

    private Properties systemProperties;
    private Map<String, String> systemValues;

    public SystemEnvironmentVariables() {
        this(System.getProperties(), System.getenv());
    }

    protected SystemEnvironmentVariables(Properties systemProperties, Map<String, String> systemValues) {
        this.systemProperties = PropertiesUtil.copyOf(systemProperties);
        this.systemValues = new HashMap<>(systemValues);
    }

    public String getValue(final String name) {
        return getValue(name, null);
    }

    public String getValue(Enum<?> property) {
        return getValue(property.toString());
    }

    public String getValue(final String name, final String defaultValue) {
        String value = systemValues.get(name);
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }


    public String getValue(Enum<?> property, String defaultValue) {
        return getValue(property.toString(), defaultValue);
    }

    public List<String> getKeys() {
        return Lambda.convert(systemProperties.keySet(), new DefaultStringConverter());
    }

    @Override
    public Properties getProperties() {
        return new Properties(systemProperties);
    }

    @Override
    public Properties getPropertiesWithPrefix(String prefix) {
        Properties filteredProperties = new Properties();
        for (String key : systemProperties.stringPropertyNames()) {
            if (key.startsWith(prefix)) {
                filteredProperties.put(key, systemProperties.getProperty(key));
            }
        }
        return filteredProperties;
    }

    @Override
    public boolean aValueIsDefinedFor(Enum<?> property) {
        return aValueIsDefinedFor(property.toString());
    }

    @Override
    public boolean aValueIsDefinedFor(String property) {
        return systemProperties.contains(property);
    }

    public Integer getPropertyAsInteger(String property, Integer defaultValue) {
        String value = (String) systemProperties.get(property);
        if (value != null) {
            return Integer.valueOf(value);
        } else {
            return defaultValue;
        }
    }


    public Integer getPropertyAsInteger(Enum<?> property, Integer defaultValue) {
        return getPropertyAsInteger(property.toString(), defaultValue);
    }

    public Boolean getPropertyAsBoolean(String name, boolean defaultValue) {
        if (getProperty(name) == null) {
            return defaultValue;
        } else if (StringUtils.isBlank(getProperty(name))) {
            return true;
        } else {
            return Boolean.parseBoolean(getProperty(name, "false"));
        }
    }


    public Boolean getPropertyAsBoolean(Enum<?> property, boolean defaultValue) {
        return getPropertyAsBoolean(property.toString(), defaultValue);
    }

    public String getProperty(final String name) {
        return (String) systemProperties.get(name);
    }


    public String getProperty(Enum<?> property) {
        return getProperty(property.toString());
    }

    public String getProperty(final String name, final String defaultValue) {
        return systemProperties.getProperty(name, defaultValue);
    }


    public String getProperty(Enum<?> property, String defaultValue) {
        return getProperty(property.toString(), defaultValue);
    }


    public void setProperty(String name, String value) {
        systemProperties.setProperty(name, value);
    }


    public void clearProperty(String name) {
        systemProperties.remove(name);
    }

    public EnvironmentVariables copy() {
        return new SystemEnvironmentVariables(systemProperties, systemValues);
    }

    public static EnvironmentVariables createEnvironmentVariables() {
        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        LocalPreferences localPreferences = new PropertiesFileLocalPreferences(environmentVariables);
        try {
            localPreferences.loadPreferences();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return environmentVariables;
    }
}
