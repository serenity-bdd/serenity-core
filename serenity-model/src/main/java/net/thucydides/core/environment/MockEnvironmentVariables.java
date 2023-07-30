package net.thucydides.core.environment;

import com.typesafe.config.Config;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class MockEnvironmentVariables implements EnvironmentVariables {

    private Map<String, String> properties = new HashMap<>();
    private Map<String, String> values = new HashMap<>();

    public MockEnvironmentVariables() {
        this.properties.put("user.home", System.getProperty("user.home"));
        this.properties.put("feature.file.encoding", "UTF-8");
        if (localEnvironment().getProperty("phantomjs.binary.path") != null) {
            this.properties.put("phantomjs.binary.path", localEnvironment().getProperty("phantomjs.binary.path"));
        }
        if (localEnvironment().getProperty("webdriver.chrome.driver") != null) {
            this.properties.put("webdriver.chrome.driver", localEnvironment().getProperty("webdriver.chrome.driver"));
        }
    }

    private EnvironmentVariables localEnvironment() {
        return ConfiguredEnvironment.getEnvironmentVariables();
    }

    protected MockEnvironmentVariables(Map<String, String> properties) {
        this.properties = new HashMap<>(properties);
    }

    protected MockEnvironmentVariables(Map<String, String> properties, Map<String, String> values) {
        this.properties = new HashMap<>(properties);
        this.values = new HashMap<>(values);
    }

    public static EnvironmentVariables fromSystemEnvironment() {
        return new MockEnvironmentVariables(SystemEnvironmentVariables.createEnvironmentVariables().properties());
    }

    public boolean propertySetIsEmpty() {
        return properties.isEmpty();
    }

    public String getValue(String name) {
        return values.get(name);
    }


    public String getValue(Enum<?> property) {
        return getValue(property.toString());
    }

    public String getValue(String name, String defaultValue) {
        return values.get(name) == null ? defaultValue : values.get(name);
    }

    public String getValue(Enum<?> property, String defaultValue) {
        return getValue(property.toString(), defaultValue);
    }

    public Integer getPropertyAsInteger(String name, Integer defaultValue) {
        String value = properties.get(name);
        if (StringUtils.isNumeric(value)) {
            return Integer.parseInt(properties.get(name));
        } else {
            return defaultValue;
        }
    }


    public Integer getPropertyAsInteger(Enum<?> property, Integer defaultValue) {
        return getPropertyAsInteger(property.toString(), defaultValue);
    }

    public Boolean getPropertyAsBoolean(String name, boolean defaultValue) {
        if (properties.get(name) == null) {
            return defaultValue;
        } else {
            return Boolean.parseBoolean(properties.get(name));
        }
    }


    public Boolean getPropertyAsBoolean(Enum<?> property, boolean defaultValue) {
        return getPropertyAsBoolean(property.toString(), defaultValue);
    }

    public String getProperty(String name) {
        if (name != null) {
            return properties.get(name);
        } else {
            return null;
        }
    }

    @Override
    public Optional<String> optionalProperty(String name) {
        return Optional.ofNullable(getProperty(name));
    }


    public String getProperty(Enum<?> property) {
        return getProperty(property.toString());
    }

    public String getProperty(String name, String defaultValue) {
        return properties.get(name) == null ? defaultValue : properties.get(name);
    }


    public String getProperty(Enum<?> property, String defaultValue) {
        return getProperty(property.toString(), defaultValue);
    }

    public void setProperty(String name, String value) {
        properties.put(name, value);
    }

    public void setProperties(Map<String, String> newProperties) {
        properties.putAll(newProperties);
    }


    public void clearProperty(String name) {
        properties.remove(name);
    }

    public EnvironmentVariables copy() {
        return new MockEnvironmentVariables(properties, values);
    }

    @Override
    public List<String> getKeys() {
        return properties.keySet().stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    public Properties getProperties() {
        Properties props = new Properties();
        props.putAll(properties);
        return props;
    }

    @Override
    public Properties getPropertiesWithPrefix(String prefix) {
        return new SystemEnvironmentVariables(properties, values).getPropertiesWithPrefix(prefix);
    }

    @Override
    public boolean aValueIsDefinedFor(Enum<?> property) {
        return properties.containsKey(property.toString());
    }

    @Override
    public boolean aValueIsDefinedFor(String property) {
        return properties.containsKey(property);
    }

    @Override
    public boolean hasPath(String path) {
        return false;
    }

    @Override
    public String injectSystemPropertiesInto(String value) {
        return value;
    }

    @Override
    public Map<String, String> asMap() {
        Map<String, String> environmentValues = new HashMap<>();

        values.keySet().forEach(
                key -> environmentValues.put(key, values.get(key))
        );
        properties.keySet().forEach(
                key -> environmentValues.put(key, properties.get(key))
        );
        return environmentValues;
    }

    @Override
    public Map<String, String> simpleSystemPropertiesAsMap() {
        return new HashMap<>();
    }

    @Override
    public void reset() {

    }

    @Override
    public void setConfig(Config typesafeConfig) {
    }

    @Override
    public Config getConfig(String prefix) {
        return EnvironmentVariables.super.getConfig(prefix);
    }

    @Override
    public Map<String, String> properties() {
        return properties;
    }

    public void setValue(String name, String value) {
        values.put(name, value);
    }

}
