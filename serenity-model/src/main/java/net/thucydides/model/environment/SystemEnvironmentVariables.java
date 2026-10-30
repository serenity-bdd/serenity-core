package net.thucydides.model.environment;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.model.util.LocalPreferences;
import net.thucydides.model.util.PropertiesLocalPreferences;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

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

    private static EnvironmentVariables CACHED_ENVIRONMENT_VARIABLES;

    private static final Lock lock = new ReentrantLock();

    private static EnvironmentVariables getCachedEnvironmentVariables() {
        if (CACHED_ENVIRONMENT_VARIABLES == null) {
            lock.lock();
            try {
                if (CACHED_ENVIRONMENT_VARIABLES == null) { // Check again inside the lock
                    CACHED_ENVIRONMENT_VARIABLES = createEnvironmentVariables().loadLocalConfig();
                    warnIfSerenityConfIsOutsideTheClasspath();
                }
            } finally {
                lock.unlock();
            }
        }
        return CACHED_ENVIRONMENT_VARIABLES;
    }

    /**
     * serenity.conf is read from the classpath (src/test/resources), not from the project directory, so a copy in
     * the project root is silently ignored. Say so once, because it is a common reason for "my configuration is ignored".
     */
    private static void warnIfSerenityConfIsOutsideTheClasspath() {
        try {
            java.io.File inProjectDirectory = new java.io.File("serenity.conf");
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader() != null
                    ? Thread.currentThread().getContextClassLoader() : SystemEnvironmentVariables.class.getClassLoader();
            if (inProjectDirectory.isFile() && classLoader.getResource("serenity.conf") == null) {
                LoggerFactory.getLogger(SystemEnvironmentVariables.class).warn(
                        "Found {} but Serenity reads serenity.conf from the classpath, not from the project directory, so it is ignored. "
                                + "Move it to src/test/resources/serenity.conf.", inProjectDirectory.getAbsolutePath());
            }
        } catch (RuntimeException ignored) {
            // Never let a diagnostic stop the configuration from loading
        }
    }

    private SystemEnvironmentVariables(Map<String, String> properties, Map<String, String> systemValues, Config config) {
        this.properties.putAll(properties);
        this.systemValues.putAll(systemValues);
        this.config = config;
        loadLocalConfig();
    }

    private void clearConfig() {
        this.config = null;
        this.configLoaded = false;
    }

    public EnvironmentVariables copy() {
        return new SystemEnvironmentVariables(this.properties, this.systemValues, this.config);
    }

    public SystemEnvironmentVariables() {
        this(System.getProperties(), System.getenv());
    }

    public static EnvironmentUpdater currentEnvironment() {
        return new EnvironmentUpdater(getCachedEnvironmentVariables());
    }

    /**
     * Get the current environment variables, including any values updated for the scope of this test.
     * Test-local environment variables can be updated using the TestLocalEnvironmentVariables class.
     */
    public static EnvironmentVariables currentEnvironmentVariables() {
        if (TestLocalEnvironmentVariables.isEmpty()) {
            return getCachedEnvironmentVariables();
        } else {
            return TestLocalEnvironmentVariables.getUpdatedEnvironmentVariables();
        }
    }

    public void setConfig(Config typesafeConfig) {
        this.config = typesafeConfig.resolve();
    }

    public SystemEnvironmentVariables(Map<String, String> propertyValues, Map<String, String> systemValues) {
        this.systemValues.putAll(systemValues);
        this.properties.putAll(propertyValues);
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
        String value = properties.get(name);
        return (value != null) ? value : environmentSpecificValueFor(name);
    }

    /**
     * A value defined inside {@code environments { }} for the active environment, so that
     * {@code getProperty("app.url")} finds a value that is defined only for an environment. A property defined outside
     * {@code environments { }}, including one given with -D on the command line, always wins over these values.
     * The order is the one {@link net.serenitybdd.model.environment.EnvironmentSpecificConfiguration} uses: the active
     * environment(s), then "all", then "default".
     */
    private String environmentSpecificValueFor(String name) {
        // Reading "environment" or the environments themselves must not look for environment-specific values
        if ("environment".equals(name) || name.startsWith("environments.")) {
            return null;
        }
        String value = null;
        String activeEnvironments = properties.get("environment");
        if (activeEnvironments != null) {
            for (String environment : activeEnvironments.split(",")) {
                String candidate = properties.get("environments." + environment.trim() + "." + name);
                if (!environment.trim().isEmpty() && candidate != null && !candidate.isEmpty()) {
                    value = candidate;
                }
            }
        }
        if (value == null) {
            value = properties.get("environments.all." + name);
        }
        if (value == null) {
            value = properties.get("environments.default." + name);
        }
        return value;
    }

    @Override
    public Optional<String> optionalProperty(String name) {
        return Optional.ofNullable(getProperty(name));
    }


    public String getProperty(Enum<?> property) {
        return getProperty(property.toString());
    }

    public String getProperty(final String name, final String defaultValue) {
        String value = getProperty(name);
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
        TestLocalEnvironmentVariables.clear();
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
