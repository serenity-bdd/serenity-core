package net.serenitybdd.core.environment;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EnvironmentSpecificConfiguration {
    private EnvironmentVariables environmentVariables;
    private EnvironmentStrategy environmentStrategy;

    public Properties getPropertiesWithPrefix(String prefix) {

        List<String> propertyNames = environmentVariables.getKeys().stream()
                .filter(this::propertyMatchesEnvironment)
                .filter(key -> propertyHasPrefix(key, prefix))
                .collect(Collectors.toList());

        Properties propertiesWithPrefix = new Properties();
        propertyNames.forEach(
                propertyName -> {
                    getOptionalProperty(propertyName).ifPresent(
                            propertyValue -> propertiesWithPrefix.setProperty(stripEnvironmentPrefixFrom(propertyName), propertyValue)
                    );
                }
        );
        return propertiesWithPrefix;
    }

    private boolean propertyMatchesEnvironment(String key) {
        String environment = environmentVariables.getProperty("environment");
        return (key.startsWith("environments." + environment + ".") || !isEnvironmentSpecific(key));
    }

    private boolean isEnvironmentSpecific(String key) {
        return Pattern.compile(ENVIRONMENT_PREFIX).matcher(key).find();
    }

    private static final String ENVIRONMENT_PREFIX = "environments\\.([^.]*)\\.";

    private String stripEnvironmentPrefixFrom(String key) {
        return key.replaceFirst(ENVIRONMENT_PREFIX, "");
    }

    private boolean propertyHasPrefix(String key, String prefix) {
        String regexPrefix = prefix.replaceAll("\\.","\\\\.");
        Pattern propertyWithPrefix = Pattern.compile("environments\\.([^.]*)\\." + regexPrefix + "(.*)");
        return key.startsWith(prefix) || propertyWithPrefix.matcher(key).matches();
    }

    enum EnvironmentStrategy {
        USE_NORMAL_PROPERTIES,
        USE_DEFAULT_PROPERTIES,
        ENVIRONMENT_CONFIGURED_AND_NAMED,
        ENVIRONMENT_CONFIGURED_BUT_NOT_NAMED,
        USE_DEFAULT_CONFIGURATION,
        NO_ENVIRONMENT_DEFINED
    }

    private Function<String, String> contextlessProperty = property -> environmentVariables.getProperty(property);

    private Function<String, String> defaultProperty = property -> {

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

    private Function<String, String> propertyForADefinedEnvironment = property -> {
        String environmentProperty = environmentVariables.getProperty("environments." + getDefinedEnvironment(environmentVariables) + "." + property);
        if (environmentProperty == null) {
            environmentProperty = propertyForAllEnvironments(property);
        }

        return (environmentProperty == null) ? defaultProperty.apply(property) : environmentProperty;
    };

    private String propertyForAllEnvironments(String propertyName) {
        return environmentVariables.getProperty("environments.all." + propertyName);
    }

    private String propertyForDefaultEnvironment(String propertyName) {
        return environmentVariables.getProperty("environments.default." + propertyName);
    }


    private static String getDefinedEnvironment(EnvironmentVariables environmentVariables) {
        return environmentVariables.getProperty("environment");
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
                                + getDefinedEnvironment(environmentVariables) + "'")
        );
    }

    public Optional<String> getOptionalProperty(final ThucydidesSystemProperty propertyName) {
        return getOptionalProperty(propertyName.getPropertyName(), propertyName.getLegacyPropertyName());
    }

    public Optional<String> getOptionalProperty(String... propertyNames) {

        String propertyValue =  null;
        for(String propertyName : propertyNames) {
            propertyValue = getPropertyValue(propertyName);
            if (propertyValue !=  null) { break; }
        }

        if (propertyValue == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(substituteProperties(propertyValue));
    }

    private final Pattern VARIABLE_EXPRESSION_PATTERN = Pattern.compile("#\\{([^}]*)\\}");

    private String substituteProperties(String propertyValue) {
        if (propertyValue == null) { return propertyValue; }

        Matcher matcher = VARIABLE_EXPRESSION_PATTERN.matcher(propertyValue);
        while (matcher.find()) {
            String nestedProperty = matcher.group().substring(2,matcher.group().length() - 1);
            String value = getPropertyValue(nestedProperty);
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

    private static EnvironmentStrategy environmentStrategyDefinedIn(EnvironmentVariables environmentVariables) {
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

    private static boolean specifiedEnvironmentNotConfiguredIn(EnvironmentVariables environmentVariables) {
        String environment = getDefinedEnvironment(environmentVariables);
        return ((environment != null) && (environmentVariables.getPropertiesWithPrefix("environments." + environment + ".")).isEmpty());
    }
}
