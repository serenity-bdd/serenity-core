package net.thucydides.model.environment;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.serenitybdd.model.collect.NewMap;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.model.util.LocalPreferences;
//import net.thucydides.model.util.PropertiesFileLocalPreferences;
import net.thucydides.model.util.PropertiesLocalPreferences;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Return system environment variable values.
 */
public class SystemEnvironmentVariables implements EnvironmentVariables {

    private final Map<String, String> properties = new ConcurrentHashMap<>();
    private final Map<String, String> systemValues = new ConcurrentHashMap<>();
    private volatile Config config;
    private volatile boolean configLoaded = false;

    private static SystemEnvironmentVariables CACHED_ENVIRONMENT_VARIABLES;

    private static SystemEnvironmentVariables getCachedEnvironmentVariables() {
        if (CACHED_ENVIRONMENT_VARIABLES == null) {
            CACHED_ENVIRONMENT_VARIABLES = createEnvironmentVariables();
        }
        return CACHED_ENVIRONMENT_VARIABLES;
    }

    /**
     * System properties as loaded from the system, without test-specific configuration
     */
    private Map<String, String> pristineProperties = new ConcurrentHashMap<>();

    private SystemEnvironmentVariables(Map<String, String> properties, Map<String, String> systemValues, Config config, Map<String, String> pristineProperties) {
        this.properties.putAll(properties);
        this.systemValues.putAll(systemValues);
        this.config = config;
        this.pristineProperties.putAll(pristineProperties);
        loadLocalConfig();
    }

    private void clearConfig() {
        this.config = null;
        this.configLoaded = false;
    }

    public EnvironmentVariables copy() {
        return new SystemEnvironmentVariables(this.properties, this.systemValues, this.config, this.pristineProperties).loadLocalConfig();
    }

    public SystemEnvironmentVariables() {
        this(System.getProperties(), System.getenv());
    }

    public static EnvironmentUpdater currentEnvironment() {
        return new EnvironmentUpdater(getCachedEnvironmentVariables().loadLocalConfig());
    }

    /**
     * Get the current environment variables, including any values updated for the scope of this test.
     * Test-local environment variables can be updated using the TestLocalEnvironmentVariables class.
     */
    public static EnvironmentVariables currentEnvironmentVariables() {
        return getCachedEnvironmentVariables();
    }

    public void setConfig(Config typesafeConfig) {
        this.config = typesafeConfig.resolve();
    }

    public SystemEnvironmentVariables(Map<String, String> propertyValues, Map<String, String> systemValues) {
        this.systemValues.putAll(systemValues);
        this.properties.putAll(propertyValues);
        this.pristineProperties = NewMap.copyOf(propertyValues);
        loadLocalConfig();
    }

    public SystemEnvironmentVariables(Properties systemProperties, Map<String, String> systemValues) {
        this.systemValues.putAll(systemValues);

        Map<String, String> propertyValues = new HashMap<>();
        for (String property : systemProperties.stringPropertyNames()) {
            String value = systemProperties.getProperty(property);
            propertyValues.put(property, value);
        }
        this.properties.putAll(propertyValues);
        this.pristineProperties = NewMap.copyOf(propertyValues);
        loadLocalConfig();
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

    private void setValue(String name, String value) {
        systemValues.put(name, value);
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
    public boolean hasPath(String path) {
        return ((config != null) && config.hasPath(path));
    }

    @Override
    public String injectSystemPropertiesInto(String value) {
        if (value == null) {
            return value;
        }
        if (!value.contains("${")) {
            return value;
        }
        for (String key : systemValues.keySet()) {
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

    @Override
    public Optional<String> optionalProperty(String name) {
        return Optional.ofNullable(getProperty(name));
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
        properties.put(name, value);
        propertySetLock.unlock();
    }

    public void setProperties(Map<String, String> properties) {
        propertySetLock.lock();
        this.properties.putAll(properties);
        propertySetLock.unlock();
    }


    public void clearProperty(String name) {
        propertySetLock.lock();
        properties.remove(name);
        propertySetLock.unlock();
    }

    @Override
    public Map<String, String> asMap() {
        Map<String, String> environmentValues = new HashMap<>(properties);
        environmentValues.putAll(systemValues);
        return environmentValues;
    }

    @Override
    public Map<String, String> simpleSystemPropertiesAsMap() {
        Map<String, String> environmentValues = new HashMap<>();
        properties.keySet().stream()
                .filter(key -> !key.contains("."))
                .forEach(
                        key -> environmentValues.put(key, properties.get(key))
                );
        return environmentValues;
    }

    @Override
    public void reset() {
        this.properties.clear();
        this.properties.putAll(pristineProperties);
    }

    public static SystemEnvironmentVariables createEnvironmentVariables() {
        return createEnvironmentVariables(new SystemEnvironmentVariables());
    }

    public static SystemEnvironmentVariables createEnvironmentVariables(Path configurationFile) {
        return createEnvironmentVariables(configurationFile, new SystemEnvironmentVariables().withEmptyConfig());
    }

    private SystemEnvironmentVariables withEmptyConfig() {
        this.config = null;
        this.configLoaded = false;
        return this;
    }

    public static SystemEnvironmentVariables createEnvironmentVariables(Path configurationFile, SystemEnvironmentVariables environmentVariables) {
        LocalPreferences localPreferences = new PropertiesLocalPreferences(environmentVariables.properties, configurationFile);
        try {
            localPreferences.loadPreferences();
            environmentVariables.setConfig(localPreferences.getConfig());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return environmentVariables;
    }

    private static SystemEnvironmentVariables createEnvironmentVariables(SystemEnvironmentVariables environmentVariables) {
        LocalPreferences localPreferences = new PropertiesLocalPreferences(environmentVariables.properties);
        try {
            localPreferences.loadPreferences();
            environmentVariables.setConfig(localPreferences.getConfig());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return environmentVariables;
    }

    private EnvironmentVariables loadLocalConfig() {
        if (!configLoaded) {
            LocalPreferences localPreferences = new PropertiesLocalPreferences(this.properties);
            try {
                localPreferences.loadPreferences();
                this.setConfig(localPreferences.getConfig());
            } catch (IOException e) {
                e.printStackTrace();
            }
            configLoaded = true;
        }
        return this;
    }

    public Config getConfig(String prefix) {
        if (config != null && config.hasPath(prefix)) {
            return config.getConfig(prefix);
        }
        return ConfigFactory.empty();
    }

    @Override
    public Map<String, String> properties() {
        return properties;
    }

    public static class EnvironmentUpdater {
        private final EnvironmentVariables environmentVariables;

        public EnvironmentUpdater(EnvironmentVariables environmentVariables) {
            this.environmentVariables = environmentVariables;
        }

        public void setProperty(String name, String value) {
            environmentVariables.setProperty(name, value);
        }

        public void reset() {
            environmentVariables.reset();
        }
    }
}
