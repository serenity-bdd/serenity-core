package net.thucydides.model.environment;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.util.EnvironmentVariables;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private static final String CONF = "app.url = \"https://top-level.example.com\"\n"
            + "environments {\n"
            + "  default { app.url = \"https://dev.example.com\" }\n"
            + "  staging { app.url = \"https://staging.example.com\" }\n"
            + "}\n";

    private EnvironmentVariables environmentFromFile(String environment) throws IOException {
        Path conf = Files.createTempFile("serenity", ".conf");
        Files.writeString(conf, CONF);
        EnvironmentVariables env = SystemEnvironmentVariables.createEnvironmentVariables(conf);
        if (environment != null) {
            env.setProperty("environment", environment);
        }
        return env;
    }

    @Test
    void anEnvironmentValueWinsOverAPlainValueReadFromTheConfigurationFile() throws IOException {
        EnvironmentVariables env = environmentFromFile("staging");
        // the file also defines a plain app.url (top-level.example.com); the environment value wins
        assertThat(env.properties().get("app.url")).isEqualTo("https://top-level.example.com");
        assertThat(env.getProperty("app.url")).isEqualTo("https://staging.example.com");
        assertThat(EnvironmentSpecificConfiguration.from(env).getOptionalProperty("app.url")).contains("https://staging.example.com");
    }

    @Test
    void aValueGivenExplicitlyOverridesTheEnvironmentValueInBothApis() throws IOException {
        EnvironmentVariables env = environmentFromFile("staging");
        env.setProperty("app.url", "https://given-with-D.example.com");

        assertThat(env.isExplicitlyDefined("app.url")).isTrue();
        assertThat(env.getProperty("app.url")).isEqualTo("https://given-with-D.example.com");
        assertThat(EnvironmentSpecificConfiguration.from(env).getOptionalProperty("app.url")).contains("https://given-with-D.example.com");
    }

    @Test
    void aValueFromTheConfigurationFileIsNotExplicit() throws IOException {
        EnvironmentVariables env = environmentFromFile("staging");

        assertThat(env.isExplicitlyDefined("environments.staging.app.url")).isFalse();
    }

    @Test
    void clearingAnExplicitValueRestoresTheEnvironmentValue() throws IOException {
        EnvironmentVariables env = environmentFromFile("staging");
        env.setProperty("app.url", "https://given-with-D.example.com");
        env.clearProperty("app.url");

        // clearing removes the explicit value, and with it the plain value from the file
        assertThat(EnvironmentSpecificConfiguration.from(env).getOptionalProperty("app.url")).contains("https://staging.example.com");
    }

    @Test
    void aSystemPropertyGivenInTheConstructorIsExplicit() {
        EnvironmentVariables env = environmentWith(
                "environment", "staging",
                "app.url", "https://given-with-D.example.com",
                "environments.staging.app.url", "https://staging.example.com");

        assertThat(env.isExplicitlyDefined("app.url")).isTrue();
        assertThat(EnvironmentSpecificConfiguration.from(env).getOptionalProperty("app.url")).contains("https://given-with-D.example.com");
    }
}
