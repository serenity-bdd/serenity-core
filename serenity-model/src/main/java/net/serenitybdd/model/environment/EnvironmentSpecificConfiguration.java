package net.serenitybdd.model.environment;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EnvironmentSpecificConfiguration {
    private EnvironmentVariables environmentVariables;
    private final EnvironmentStrategy environmentStrategy;

    public Properties getPropertiesWithPrefix(String prefix) {
        return getPropertiesWithPrefix(prefix, false);
    }

    public Properties getPropertiesWithPrefixStripped(String prefix) {
        return getPropertiesWithPrefix(prefix, true);
    }

    public Properties getPropertiesWithPrefix(String prefix, boolean stripPrefix) {

        List<String> propertyNames = environmentVariables.getKeys().stream()
                .filter(this::propertyMatchesEnvironment)
                .filter(key -> propertyHasPrefix(key, prefix))
                .collect(Collectors.toList());

        Properties propertiesWithPrefix = stripEnvironmentPrefixFrom(propertyNames);
        if (stripPrefix) {
            propertiesWithPrefix = stripPrefixFrom(propertyNames, prefix);
        }
        return propertiesWithPrefix;
    }

    private Properties stripEnvironmentPrefixFrom(List<String> propertyNames) {
        Properties propertiesWithPrefix = new Properties();
        propertyNames.forEach(
                propertyName ->
                        getOptionalProperty(propertyName).ifPresent(
                                propertyValue -> propertiesWithPrefix.setProperty(stripEnvironmentPrefixFrom(propertyName), propertyValue)
                        )
        );
        return propertiesWithPrefix;
    }


    private Properties stripPrefixFrom(List<String> propertyNames, String prefix) {
        Properties propertiesWithPrefix = new Properties();
        propertyNames.forEach(
                propertyName ->
                        getOptionalProperty(propertyName).ifPresent(
                                propertyValue -> propertiesWithPrefix.setProperty(stripPrefixFrom(propertyName, prefix), propertyValue)
                        )
        );
        return propertiesWithPrefix;
    }

    private boolean propertyMatchesEnvironment(String key) {
        if (!isEnvironmentSpecific(key)) {
            return true;
        }
        return activeEnvironments().stream().anyMatch(
                environment -> (key.startsWith("environments." + environment + "."))
        );
    }

    private List<String> activeEnvironments() {
        return activeEnvironmentsIn(environmentVariables);
    }

    public static List<String> activeEnvironmentsIn(EnvironmentVariables environmentVariables) {
        return environmentVariables.activeEnvironments();
    }

    private static final String ENVIRONMENT_PREFIX = "environments\\.([^.]*)\\.";
    private final Pattern ENV_PREFIX_REGEX = Pattern.compile(ENVIRONMENT_PREFIX);

    private boolean isEnvironmentSpecific(String key) {
        return ENV_PREFIX_REGEX.matcher(key).find();
    }

    private String stripEnvironmentPrefixFrom(String key) {
        return key.replaceFirst(ENVIRONMENT_PREFIX, "");
    }

    private String stripPrefixFrom(String key, String prefix) {
        return key.replaceFirst(prefix + ".", "");
    }

    private boolean propertyHasPrefix(String key, String prefix) {
        String regexPrefix = prefix.replaceAll("\\.", "\\\\.");
        Pattern propertyWithPrefix = Pattern.compile("environments\\.([^.]*)\\." + regexPrefix + "(.*)");
        return key.startsWith(prefix) || propertyWithPrefix.matcher(key).matches();
    }

    public Config getConfig(String prefix) {
        // Look for an environment-specific section
        List<String> activeEnvironments = environmentVariables.activeEnvironments();

        Optional<String> activePrefix = activeEnvironments.stream()
                .filter(
                        activeEnvironment -> {
                            String qualifiedPrefix = "environments." + activeEnvironment + "." + prefix;
                            return !activeEnvironment.isEmpty() && environmentVariables.hasPath(qualifiedPrefix);
                        }
                ).map(activeEnvironment -> "environments." + activeEnvironment + "." + prefix)
                .findFirst();

        if (activePrefix.isPresent()) {
            return environmentVariables.getConfig(activePrefix.get());
        } else if (environmentVariables.hasPath(prefix)) {
            // Use default value without the environment prefix if available
            return environmentVariables.getConfig(prefix);
        } else {
            return ConfigFactory.empty();
        }
    }

    enum EnvironmentStrategy {
        USE_NORMAL_PROPERTIES,
        USE_DEFAULT_PROPERTIES,
        ENVIRONMENT_CONFIGURED_AND_NAMED,
        ENVIRONMENT_CONFIGURED_BUT_NOT_NAMED,
        USE_DEFAULT_CONFIGURATION,
        NO_ENVIRONMENT_DEFINED
    }

    private final Function<String, String> contextlessProperty = property -> environmentVariables.getProperty(property);

    private final Function<String, String> defaultProperty = property -> {

        String candidateValue = propertyForAllEnvironments(property);
        if (candidateValue == null) {
            candidateValue = propertyForDefaultEnvironment(property);
        }
        if (candidateValue == null) {
            candidateValue = contextlessProperty.apply(property);
        }
        return substituteProperties(candidateValue);
    };

    public static boolean areDefinedIn(EnvironmentVariables environmentVariables) {
        return !environmentVariables.getPropertiesWithPrefix("environments.").isEmpty();
    }

    private final Function<String, String> propertyForADefinedEnvironment = property -> {
        String environmentProperty = null;

        for (String environment : activeEnvironments()) {
            environmentProperty = override(environmentProperty, environmentVariables.getProperty("environments." + environment + "." + property));
        }

        if (environmentProperty == null) {
            environmentProperty = propertyForAllEnvironments(property);
        }

        return (environmentProperty == null) ? defaultProperty.apply(property) : environmentProperty;
    };

    private String override(String currentValue, String newValue) {
        if ((newValue != null) && (!newValue.isEmpty())) {
            return newValue;
        } else {
            return currentValue;
        }
    }

    private String propertyForAllEnvironments(String propertyName) {
        return environmentVariables.getProperty("environments.all." + propertyName);
    }

    private String propertyForDefaultEnvironment(String propertyName) {
        return environmentVariables.getProperty("environments.default." + propertyName);
    }

    public EnvironmentSpecificConfiguration(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.environmentStrategy = environmentStrategyDefinedIn(environmentVariables);
    }

    public String getProperty(final String propertyName) {

        return getOptionalProperty(propertyName)
                .orElseThrow(
                        () -> new UndefinedEnvironmentVariableException("Environment '"
                                + propertyName
                                + "' property undefined for environment '"
                                + activeEnvironmentsIn(environmentVariables) + "'")
                );
    }

    public Optional<String> getOptionalProperty(final ThucydidesSystemProperty propertyName) {
        return getOptionalProperty(propertyName.getPropertyName(), propertyName.getLegacyPropertyName());
    }

    public String getProperty(final ThucydidesSystemProperty propertyName) {
        return getOptionalProperty(propertyName).orElseThrow(
                () -> new UndefinedEnvironmentVariableException("Environment '"
                        + propertyName
                        + "' property undefined for environment '"
                        + activeEnvironmentsIn(environmentVariables) + "'"));
    }

    public String getNullableProperty(final ThucydidesSystemProperty propertyName) {
        return getOptionalProperty(propertyName).orElse(null);
    }

    public Integer getIntegerProperty(final ThucydidesSystemProperty propertyName) {
        return Integer.parseInt(getProperty(propertyName));
    }

    public boolean getBooleanProperty(final ThucydidesSystemProperty propertyName) {
        return getBooleanProperty(propertyName, false);
    }

    public boolean getBooleanProperty(final String propertyName) {
        return getBooleanProperty(propertyName, false);
    }

    public List<String> getListOfValues(final ThucydidesSystemProperty propertyName) {
        return Arrays.stream(getOptionalProperty(propertyName).orElse("").split(",")).map(String::trim).collect(Collectors.toList());
    }

    public boolean getBooleanProperty(final ThucydidesSystemProperty propertyName, boolean defaultValue) {
        String value = getOptionalProperty(propertyName).orElse(Boolean.toString(defaultValue));
        return Boolean.parseBoolean(value);
    }

    public boolean getBooleanProperty(final String propertyName, boolean defaultValue) {
        String value = getOptionalProperty(propertyName).orElse(Boolean.toString(defaultValue));
        return Boolean.parseBoolean(value);
    }

    public Optional<String> getOptionalProperty(List<String> possiblePropertyNames) {
        String propertyValue = null;
        for (String propertyName : possiblePropertyNames) {
            propertyValue = getPropertyValue(propertyName);
            if (propertyValue != null) {
                break;
            }
        }

        if (propertyValue == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(substituteProperties(propertyValue));
    }

    public Optional<Integer> getOptionalInteger(List<String> possiblePropertyNames) {
        String propertyValue = null;
        for (String propertyName : possiblePropertyNames) {
            propertyValue = getPropertyValue(propertyName);
            if (propertyValue != null) {
                break;
            }
        }
        if (propertyValue == null) {
            return Optional.empty();
        }
        return Optional.of(Integer.parseInt(propertyValue));
    }

    public Optional<Integer> getOptionalInteger(String... propertyNames) {
        return getOptionalInteger(Arrays.asList(propertyNames));
    }

    public Optional<String> getOptionalProperty(String... propertyNames) {
        return getOptionalProperty(Arrays.asList(propertyNames));
    }

    public Optional<String> getOptionalProperty(ThucydidesSystemProperty... properties) {
        String propertyValue = null;
        for (ThucydidesSystemProperty property : properties) {
            propertyValue = getPropertyValue(property.getPropertyName());
            if (propertyValue != null) {
                break;
            }
        }

        if (propertyValue == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(substituteProperties(propertyValue));
    }

    public boolean propertyGroupIsDefinedFor(String propertyGroupName) {
        return !this.getPropertiesWithPrefix(propertyGroupName).isEmpty();
    }

    private final Pattern VARIABLE_EXPRESSION_PATTERN = Pattern.compile("#\\{([^}]*)}");

    private String substituteProperties(String propertyValue) {
        if (propertyValue == null) {
            return null;
        }

        Matcher matcher = VARIABLE_EXPRESSION_PATTERN.matcher(propertyValue);
        while (matcher.find()) {
            String nestedProperty = matcher.group().substring(2, matcher.group().length() - 1);
            String value = getPropertyValue(nestedProperty);
            if (value == null) {
                value = EnvironmentSpecificConfiguration.from(environmentVariables).getPropertyValue(nestedProperty);
            }
            if (value != null) {
                propertyValue = matcher.replaceFirst(value);
                matcher.reset(propertyValue);
            }
        }
        return propertyValue;
    }


    public String getPropertyValue(String propertyName) {

        switch (environmentStrategy) {
            case USE_NORMAL_PROPERTIES:
            case ENVIRONMENT_CONFIGURED_BUT_NOT_NAMED:
                return contextlessProperty.apply(propertyName);

            case USE_DEFAULT_PROPERTIES:
                return defaultProperty.apply(propertyName);

            case ENVIRONMENT_CONFIGURED_AND_NAMED:
                return propertyForADefinedEnvironment.apply(propertyName);
        }

        return null;
    }


    public static EnvironmentSpecificConfiguration from(EnvironmentVariables environmentVariables) {
        return new EnvironmentSpecificConfiguration(environmentVariables);
    }

    private EnvironmentStrategy environmentStrategyDefinedIn(EnvironmentVariables environmentVariables) {
        boolean environmentsAreConfigured = !environmentVariables.getPropertiesWithPrefix("environments.").isEmpty();
        boolean environmentIsSpecified = environmentVariables.getProperty("environment") != null;
        boolean defaultEnvironmentsAreConfigured = !environmentVariables.getPropertiesWithPrefix("environments.default.").isEmpty();

        if (!environmentsAreConfigured) {
            return EnvironmentStrategy.USE_NORMAL_PROPERTIES;
        }

        if (specifiedEnvironmentNotConfiguredIn(environmentVariables)) {
            return EnvironmentStrategy.USE_DEFAULT_PROPERTIES;
        }

        if (defaultEnvironmentsAreConfigured && !environmentIsSpecified) {
            return EnvironmentStrategy.USE_DEFAULT_PROPERTIES;
        }

        if (!environmentIsSpecified) {
            return EnvironmentStrategy.ENVIRONMENT_CONFIGURED_BUT_NOT_NAMED;
        }

        return EnvironmentStrategy.ENVIRONMENT_CONFIGURED_AND_NAMED;
    }

    private boolean specifiedEnvironmentNotConfiguredIn(EnvironmentVariables environmentVariables) {
        List<String> activeEnvironments = activeEnvironmentsIn(environmentVariables);
        return activeEnvironments.stream().allMatch(
                environment -> environmentVariables.getPropertiesWithPrefix("environments." + environment + ".").isEmpty()

        );
    }
}
