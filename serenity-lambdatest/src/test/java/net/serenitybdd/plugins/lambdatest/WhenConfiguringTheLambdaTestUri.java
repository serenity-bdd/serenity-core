package net.serenitybdd.plugins.lambdatest;

import net.serenitybdd.plugins.lambdatest.LambdaTestUri;
import net.thucydides.model.environment.MockEnvironmentVariables;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WhenConfiguringTheLambdaTestUri {

    @Nested
    @DisplayName("Using system environment variables")
    class UsingTheSystemProperties {
        @Test
        @DisplayName("We can use the LT_USERNAME and LT_ACCESS_KEY system properties to define the LambdaTest Credentials")
        public void shouldUseSystemPropertiesIfDefined() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setValue("LT_USERNAME","lambdauser");
            environmentVariables.setValue("LT_ACCESS_KEY","XXXXXXXX");

            String url = LambdaTestUri.definedIn(environmentVariables).getUri();

            assertThat(url).isEqualTo("https://lambdauser:XXXXXXXX@hub.lambdatest.com/wd/hub");
        }
    }

    @Nested
    @DisplayName("Using configuration options")
    class OverridingTheProperties {
        @Test
        @DisplayName("We can define credentials using lt.user and lt.key")
        public void shouldUseSystemPropertiesIfDefined() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setProperty("lt.user","myuser");
            environmentVariables.setProperty("lt.key","MYKEY");

            String url = LambdaTestUri.definedIn(environmentVariables).getUri();

            assertThat(url).isEqualTo("https://myuser:MYKEY@hub.lambdatest.com/wd/hub");
        }

        @Test
        @DisplayName("But the system properties take precedence")
        public void systemPropertiesTakePrecedence() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setValue("LT_USERNAME","lambdauser");
            environmentVariables.setValue("LT_ACCESS_KEY","XXXXXXXX");

            environmentVariables.setProperty("lt.user","myuser");
            environmentVariables.setProperty("lt.key","MYKEY");

            String url = LambdaTestUri.definedIn(environmentVariables).getUri();

            assertThat(url).isEqualTo("https://lambdauser:XXXXXXXX@hub.lambdatest.com/wd/hub");
        }

        @Test
        @DisplayName("We can override the default grid with lt.grid")
        public void theGridUrlCanBeSet() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setValue("LT_USERNAME","lambdauser");
            environmentVariables.setValue("LT_ACCESS_KEY","XXXXXXXX");
            environmentVariables.setProperty("lt.grid","custom.hub.lambdatest.com");

            String url = LambdaTestUri.definedIn(environmentVariables).getUri();

            assertThat(url).isEqualTo("https://lambdauser:XXXXXXXX@custom.hub.lambdatest.com/wd/hub");
        }

        @Test
        @DisplayName("If remote.webdriver.url is already defined, that value will be used instead")
        public void theRemoteWebdriverURLTakesPrecedence() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setValue("LT_USERNAME","lambdauser");
            environmentVariables.setValue("LT_ACCESS_KEY","XXXXXXXX");
            environmentVariables.setProperty("lt.grid","custom.hub.lambdatest.com");
            environmentVariables.setProperty("webdriver.remote.url","my.special.lambdatest.url");

            String url = LambdaTestUri.definedIn(environmentVariables).getUri();

            assertThat(url).isEqualTo("my.special.lambdatest.url");
        }
    }
    @Nested
    @DisplayName("Using configuration options in an environment section")
    class UsingAnEnvironment {
        @Test
        @DisplayName("We can define credentials using lt.user and lt.key")
        public void shouldUseSystemPropertiesIfDefined() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setProperty("environment","myenv");
            environmentVariables.setProperty("environments.myenv.lt.user","myuser");
            environmentVariables.setProperty("environments.myenv.lt.key","MYKEY");

            String url = LambdaTestUri.definedIn(environmentVariables).getUri();

            assertThat(url).isEqualTo("https://myuser:MYKEY@hub.lambdatest.com/wd/hub");
        }

        @Test
        @DisplayName("We can override the default grid with lt.grid")
        public void theGridUrlCanBeSet() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setValue("LT_USERNAME","lambdauser");
            environmentVariables.setValue("LT_ACCESS_KEY","XXXXXXXX");
            environmentVariables.setProperty("environment","myenv");
            environmentVariables.setProperty("environments.myenv.lt.grid","custom.hub.lambdatest.com");

            String url = LambdaTestUri.definedIn(environmentVariables).getUri();

            assertThat(url).isEqualTo("https://lambdauser:XXXXXXXX@custom.hub.lambdatest.com/wd/hub");
        }

        @Test
        @DisplayName("If remote.webdriver.url is already defined, that value will be used instead")
        public void theRemoteWebdriverURLTakesPrecedence() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setValue("LT_USERNAME","lambdauser");
            environmentVariables.setValue("LT_ACCESS_KEY","XXXXXXXX");
            environmentVariables.setProperty("environment","myenv");
            environmentVariables.setProperty("environments.myenv.lt.grid","custom.hub.lambdatest.com");
            environmentVariables.setProperty("environments.myenv.webdriver.remote.url","my.special.lambdatest.url");

            String url = LambdaTestUri.definedIn(environmentVariables).getUri();

            assertThat(url).isEqualTo("my.special.lambdatest.url");
        }

        @Test
        @DisplayName("If no username is define, the URL will be null")
        public void missingUsername() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
            environmentVariables.setValue("LT_ACCESS_KEY","XXXXXXXX");

            String url = LambdaTestUri.definedIn(environmentVariables).getUri();

            assertThat(url).isNull();
        }

        @Test
        @DisplayName("If no access key is define, the URL will be null")
        public void missingAccessKey() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
            environmentVariables.setValue("LT_USERNAME","lambdauser");

            String url = LambdaTestUri.definedIn(environmentVariables).getUri();

            assertThat(url).isNull();
        }
    }
}
