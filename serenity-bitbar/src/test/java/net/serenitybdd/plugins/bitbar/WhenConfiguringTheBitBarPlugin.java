package net.serenitybdd.plugins.bitbar;

import net.thucydides.model.environment.MockEnvironmentVariables;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("When configuring the BitBar plugin in the serenity.conf file")
class WhenConfiguringTheBitBarPlugin {

    @Nested
    @DisplayName("The BitBar plugin is considered active")
    class BitBarIsActiveWhen {
        @Test
        @DisplayName("When the bitbar.active property is set to true")
        public void BitBarActiveProperty() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setProperty("bitbar.active", "true");

            assertThat(BitBarConfiguration.isActiveFor(environmentVariables)).isTrue();
        }

        @Test
        @DisplayName("When the bitbar.active property is set to true in an active environment")
        public void BitBarActivePropertyInAnEnvironment() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setProperty("environments.myenv.bitbar.active", "true");
            environmentVariables.setProperty("environment", "myenv");

            assertThat(BitBarConfiguration.isActiveFor(environmentVariables)).isTrue();
        }

        @Test
        @DisplayName("When the BitBar section contains properties")
        public void BitBarSectionNotEmpty() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setProperty("webdriver.capabilities.\"bitbar:options\".platformName", "Windows 10");

            assertThat(BitBarConfiguration.isActiveFor(environmentVariables)).isTrue();
        }

        @Test
        @DisplayName("When the BitBar section contains properties in an active environment")
        public void BitBarSectionNotEmptyInEnv() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setProperty("environments.myenv.webdriver.capabilities.\"bitbar:options\".platformName", "Windows 10");
            environmentVariables.setProperty("environment", "myenv");

            assertThat(BitBarConfiguration.isActiveFor(environmentVariables)).isTrue();
        }

        @Test
        @DisplayName("When the remote URL points to a BitBar server")
        public void remoteUrlPointsToBitBar() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setProperty("webdriver.driver", "remote");
            environmentVariables.setProperty("webdriver.remote.url", "apiKey@hub.bitbar.com/wd/hub");

            assertThat(BitBarConfiguration.isActiveFor(environmentVariables)).isTrue();
        }

        @Test
        @DisplayName("When the remote URL points to a BitBar server in an active environment")
        public void remoteUrlPointsToBitBarInEnv() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();


            environmentVariables.setProperty("environments.myenv.webdriver.driver", "remote");
            environmentVariables.setProperty("environments.myenv.webdriver.remote.url", "apiKey@hub.bitbar.com/wd/hub");
            environmentVariables.setProperty("environment", "myenv");

            assertThat(BitBarConfiguration.isActiveFor(environmentVariables)).isTrue();
        }
    }

}
