package net.thucydides.model.environment;

import net.thucydides.model.util.EnvironmentVariables;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A value that is defined only inside environments { } must be readable with getProperty(), not only through
 * EnvironmentSpecificConfiguration.
 */
class WhenReadingEnvironmentSpecificPropertiesDirectly {

    private EnvironmentVariables environmentWith(String... keysAndValues) {
        Map<String, String> properties = new HashMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            properties.put(keysAndValues[i], keysAndValues[i + 1]);
        }
        return new SystemEnvironmentVariables(properties, new HashMap<>());
    }

    @Test
    void theDefaultEnvironmentValueIsUsedWhenNoEnvironmentIsChosen() {
        EnvironmentVariables env = environmentWith(
                "environments.default.app.url", "https://dev.example.com",
                "environments.staging.app.url", "https://staging.example.com");

        assertThat(env.getProperty("app.url")).isEqualTo("https://dev.example.com");
    }

    @Test
    void theChosenEnvironmentValueIsUsed() {
        EnvironmentVariables env = environmentWith(
                "environment", "staging",
                "environments.default.app.url", "https://dev.example.com",
                "environments.staging.app.url", "https://staging.example.com");

        assertThat(env.getProperty("app.url")).isEqualTo("https://staging.example.com");
    }

    @Test
    void valuesTheChosenEnvironmentDoesNotDefineFallBackToDefault() {
        EnvironmentVariables env = environmentWith(
                "environment", "staging",
                "environments.default.db.name", "dev",
                "environments.staging.app.url", "https://staging.example.com");

        assertThat(env.getProperty("db.name")).isEqualTo("dev");
    }

    @Test
    void allAppliesInEveryEnvironment() {
        EnvironmentVariables env = environmentWith(
                "environment", "staging",
                "environments.all.company", "Acme");

        assertThat(env.getProperty("company")).isEqualTo("Acme");
    }

    @Test
    void theLaterOfSeveralActiveEnvironmentsWins() {
        EnvironmentVariables env = environmentWith(
                "environment", "staging, eu",
                "environments.staging.region", "us",
                "environments.eu.region", "eu");

        assertThat(env.getProperty("region")).isEqualTo("eu");
    }

    @Test
    void aPropertyOutsideEnvironmentsWinsOverEnvironmentValues() {
        EnvironmentVariables env = environmentWith(
                "environment", "staging",
                "app.url", "https://given-on-the-command-line.example.com",
                "environments.staging.app.url", "https://staging.example.com");

        assertThat(env.getProperty("app.url")).isEqualTo("https://given-on-the-command-line.example.com");
    }

    @Test
    void anUnknownPropertyIsStillNull() {
        EnvironmentVariables env = environmentWith(
                "environment", "staging",
                "environments.staging.app.url", "https://staging.example.com");

        assertThat(env.getProperty("something.else")).isNull();
        assertThat(env.getProperty("something.else", "fallback")).isEqualTo("fallback");
    }

    @Test
    void valuesOfAnEnvironmentThatIsNotChosenAreNotUsedWhenThereIsNoDefault() {
        EnvironmentVariables env = environmentWith("environments.staging.app.url", "https://staging.example.com");

        assertThat(env.getProperty("app.url")).isNull();
    }

    @Test
    void readingTheEnvironmentItselfDoesNotRecurse() {
        EnvironmentVariables env = environmentWith("environment", "staging");

        assertThat(env.getProperty("environment")).isEqualTo("staging");
        assertThat(env.getProperty("environments.staging.app.url")).isNull();
        assertThat(env.activeEnvironments()).containsExactly("staging");
    }
}
