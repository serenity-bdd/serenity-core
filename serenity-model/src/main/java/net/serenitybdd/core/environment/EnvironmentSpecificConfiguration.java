package net.serenitybdd.core.environment;

import net.thucydides.core.util.EnvironmentVariables;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnvironmentSpecificConfiguration {
    private EnvironmentVariables environmentVariables;
    private EnvironmentStrategy environmentStrategy;

    enum EnvironmentStrategy {
        USE_NORMAL_PROPERTIES,
        USE_DEFAULT_PROPERTIES,
        ENVIRONMENT_CONFIGURED_AND_NAMED,
        ENVIRONMENT_CONFIGURED_BUT_NOT_NAMED,
        USE_DEFAULT_CONFIGURATION,
        NO_ENVIRONMENT_DEFINED
    }

    private Function<String, String> contextlessProperty = property
            -> environmentVariables.getProperty(property);

    private Function<String, String> defaultProperty = property -> {

        String candidateValue = propertyForAllEnvironments(property);
        if (candidateValue == null) {
            candidateValue = propertyForDefaultEnvironment(property);
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


    public Optional<String> getOptionalProperty(String propertyName) {

        String propertyValue = getPropertyValue(propertyName);

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
            }
        }
        return propertyValue;
    }


    public String getPropertyValue(String propertyName) {

        switch (environmentStrategy) {
            case USE_NORMAL_PROPERTIES:
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

        ensureSpecifiedEnvironmentConfigurationExistsFor(environmentVariables);

        if (!environmentsAreConfigured) {
            return EnvironmentStrategy.USE_NORMAL_PROPERTIES;
        }

        if (defaultEnvironmentsAreConfigured && !environmentIsSpecified) {
            return EnvironmentStrategy.USE_DEFAULT_PROPERTIES;
        }

        if (environmentsAreConfigured && !environmentIsSpecified) {
            return EnvironmentStrategy.USE_NORMAL_PROPERTIES;
        }

        if (!environmentIsSpecified && defaultEnvironmentsAreConfigured) {
            return EnvironmentStrategy.USE_DEFAULT_CONFIGURATION;
        }
        if (!environmentIsSpecified) {
            return EnvironmentStrategy.ENVIRONMENT_CONFIGURED_BUT_NOT_NAMED;
        }

        return EnvironmentStrategy.ENVIRONMENT_CONFIGURED_AND_NAMED;
    }

    private static void ensureSpecifiedEnvironmentConfigurationExistsFor(EnvironmentVariables environmentVariables) {
        String environment = getDefinedEnvironment(environmentVariables);
        if ((environment != null) && (environmentVariables.getPropertiesWithPrefix("environments." + environment + ".")).isEmpty()) {
            throw new UnknownEnvironmentException("No environment configuration found for environment '" + environment + "'");
        }

    }
}
