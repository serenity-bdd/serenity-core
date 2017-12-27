package net.thucydides.core.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Return system environment variable values.
 */
public class SystemEnvironmentVariables implements EnvironmentVariables {

    private Map<String, String> properties;
    private Map<String, String> systemValues;

    public SystemEnvironmentVariables() {
        this(System.getProperties(), System.getenv());
    }

    SystemEnvironmentVariables(Properties systemProperties, Map<String, String> systemValues) {

        Map<String, String> propertyValues = new HashMap<>();
        for(String property : systemProperties.stringPropertyNames()) {
            propertyValues.put(property, systemProperties.getProperty(property));
        }

        this.properties = ImmutableMap.copyOf(propertyValues);
        this.systemValues = ImmutableMap.copyOf(systemValues);
    }

    public String getValue(final String name) {
        return getValue(name, null);
    }

    public String getValue(Enum<?> property) {
        return getValue(property.toString());
    }

    public String getValue(final String name, final String defaultValue) {
        String value = systemValues.get(name);
        return (value == null) ? defaultValue : value;
    }


    public String getValue(Enum<?> property, String defaultValue) {
        return getValue(property.toString(), defaultValue);
    }


    public List<String> getKeys() {
        return properties.keySet().stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    public Properties getProperties() {
        Properties props = new Properties();
        for (String key : properties.keySet()) {
            props.setProperty(key, properties.get(key));
        }
        return props;
    }

    @Override
    public Properties getPropertiesWithPrefix(String prefix) {
        Properties filteredProperties = new Properties();
        for (String key : properties.keySet()) {
            if (key.startsWith(prefix)) {
                filteredProperties.put(key, properties.get(key));
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
        return properties.containsKey(property);
    }

    @Override
    public String injectSystemPropertiesInto(String value) {
        if (value == null) { return value; }

        for(String key : systemValues.keySet()) {
            value = value.replace("${" + key.toUpperCase() + "}", systemValues.get(key));
        }
        return value;
    }

    public Integer getPropertyAsInteger(String property, Integer defaultValue) {
        String value = properties.get(property);
        return (value != null) ? Integer.valueOf(value) : defaultValue;
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
        return properties.get(name);
    }


    public String getProperty(Enum<?> property) {
        return getProperty(property.toString());
    }

    public String getProperty(final String name, final String defaultValue) {
        String value = properties.get(name);
        return (value != null) ? value : defaultValue;
    }

    public String getProperty(Enum<?> property, String defaultValue) {
        return getProperty(property.toString(), defaultValue);
    }

    private final Lock propertySetLock = new ReentrantLock();

    public void setProperty(String name, String value) {

        propertySetLock.lock();

        HashMap<String, String> workingCopy = Maps.newHashMap(properties);
        workingCopy.put(name, value);
        properties = ImmutableMap.copyOf(workingCopy);

        propertySetLock.unlock();
    }


    public void clearProperty(String name) {
        propertySetLock.lock();

        HashMap<String, String> workingCopy = Maps.newHashMap(properties);
        workingCopy.remove(name);
        properties = ImmutableMap.copyOf(workingCopy);

        propertySetLock.unlock();
    }

    public EnvironmentVariables copy() {
        return new SystemEnvironmentVariables(getProperties(), systemValues);
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
