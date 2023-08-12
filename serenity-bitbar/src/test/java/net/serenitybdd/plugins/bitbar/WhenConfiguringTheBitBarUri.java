package net.serenitybdd.plugins.bitbar;

import net.thucydides.model.environment.MockEnvironmentVariables;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WhenConfiguringTheBitBarUri {

    @Nested
    @DisplayName("Using system environment variables")
    class UsingTheSystemProperties {
        @Test
        @DisplayName("We can use the BITBAR_API_KEY system properties to define the BitBar Credentials")
        public void shouldUseSystemPropertiesIfDefined() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setValue("BITBAR_API_KEY", "XXXXXXXX");

            String url = BitBarUri.definedIn(environmentVariables).getUri();

            assertThat(url).isEqualTo("https://XXXXXXXX@eu-desktop-hub.bitbar.com/wd/hub");
        }
    }

    @Nested
    @DisplayName("Using configuration options")
    class OverridingTheProperties {
        @Test
        @DisplayName("We can define credentials using bitbar.apiKey")
        public void shouldUseSystemPropertiesIfDefined() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setProperty("bitbar.apiKey", "MYKEY");

            String url = BitBarUri.definedIn(environmentVariables).getUri();

            assertThat(url).isEqualTo("https://MYKEY@eu-desktop-hub.bitbar.com/wd/hub");
        }

        @Test
        @DisplayName("But the system properties take precedence")
        public void systemPropertiesTakePrecedence() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setValue("BITBAR_API_KEY", "XXXXXXXX");

            environmentVariables.setProperty("bitbar.apiKey", "MYKEY");

            String url = BitBarUri.definedIn(environmentVariables).getUri();

            assertThat(url).isEqualTo("https://XXXXXXXX@eu-desktop-hub.bitbar.com/wd/hub");
        }

        @Test
        @DisplayName("We can override the default bitbar hub with bitbar.hub")
        public void theGridUrlCanBeSet() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setValue("BITBAR_API_KEY", "XXXXXXXX");
            environmentVariables.setProperty("bitbar.hub", "custom-hub");

            String url = BitBarUri.definedIn(environmentVariables).getUri();

            assertThat(url).isEqualTo("https://XXXXXXXX@custom-hub.bitbar.com/wd/hub");
        }

        @Test
        @DisplayName("If remote.webdriver.url is already defined, that value will be used instead")
        public void theRemoteWebdriverURLTakesPrecedence() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setValue("BITBAR_API_KEY", "XXXXXXXX");
            environmentVariables.setProperty("bitbar.hub", "custom.hub.bitbar.com");
            environmentVariables.setProperty("webdriver.remote.url", "my.special.bitbar.url");

            String url = BitBarUri.definedIn(environmentVariables).getUri();

            assertThat(url).isEqualTo("my.special.bitbar.url");
        }
    }

    @Nested
    @DisplayName("Using configuration options in an environment section")
    class UsingAnEnvironment {
        @Test
        @DisplayName("We can define credentials using bitbar.apiKey")
        public void shouldUseSystemPropertiesIfDefined() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setProperty("environment", "myenv");
            environmentVariables.setProperty("environments.myenv.bitbar.apiKey", "MYKEY");

            String url = BitBarUri.definedIn(environmentVariables).getUri();

            assertThat(url).isEqualTo("https://MYKEY@eu-desktop-hub.bitbar.com/wd/hub");
        }

        @Test
        @DisplayName("We can override the default bitbar hub with bitbar.hub")
        public void theGridUrlCanBeSet() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setValue("BITBAR_API_KEY", "XXXXXXXX");
            environmentVariables.setProperty("environment", "myenv");
            environmentVariables.setProperty("environments.myenv.bitbar.hub", "us-mobile-hub");

            String url = BitBarUri.definedIn(environmentVariables).getUri();

            assertThat(url).isEqualTo("https://XXXXXXXX@us-mobile-hub.bitbar.com/wd/hub");
        }

        @Test
        @DisplayName("If remote.webdriver.url is already defined, that value will be used instead")
        public void theRemoteWebdriverURLTakesPrecedence() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setValue("BITBAR_API_KEY", "XXXXXXXX");
            environmentVariables.setProperty("environment", "myenv");
            environmentVariables.setProperty("environments.myenv.bitbar.hub", "us-mobile-hub");
            environmentVariables.setProperty("environments.myenv.webdriver.remote.url", "my.special.bitbar.url");

            String url = BitBarUri.definedIn(environmentVariables).getUri();

            assertThat(url).isEqualTo("my.special.bitbar.url");
        }

        @Test
        @DisplayName("If no access key is define, the URL will be null")
        public void missingApiKey() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            String url = BitBarUri.definedIn(environmentVariables).getUri();

            assertThat(url).isNull();
        }
    }
}
