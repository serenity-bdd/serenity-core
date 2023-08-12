package net.serenitybdd.plugins.lambdatest;

import net.serenitybdd.plugins.lambdatest.LambdaTestConfiguration;
import net.thucydides.model.environment.MockEnvironmentVariables;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("When configuring the LambdaTest plugin in the serenity.conf file")
class WhenConfiguringTheLambdaTestPlugin {

    @Nested
    @DisplayName("The LambdaTest plugin is considered active")
    class LambdaTestIsActiveWhen {
        @Test
        @DisplayName("When the lambdatest.active property is set to true")
        public void lambdatestActiveProperty() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setProperty("lambdatest.active","true");

            assertThat(LambdaTestConfiguration.isActiveFor(environmentVariables)).isTrue();
        }

        @Test
        @DisplayName("When the lambdatest.active property is set to true in an active environment")
        public void lambdatestActivePropertyInAnEnvironment() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setProperty("environments.myenv.lambdatest.active","true");
            environmentVariables.setProperty("environment","myenv");

            assertThat(LambdaTestConfiguration.isActiveFor(environmentVariables)).isTrue();
        }

        @Test
        @DisplayName("When the lambdatest section contains properties")
        public void lambdatestSectionNotEmpty() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setProperty("webdriver.capabilities.\"LT:Options\".platformName","Windows 11");

            assertThat(LambdaTestConfiguration.isActiveFor(environmentVariables)).isTrue();
        }

        @Test
        @DisplayName("When the lambdatest section contains properties in an active environment")
        public void lambdatestSectionNotEmptyInEnv() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setProperty("environments.myenv.webdriver.capabilities.\"LT:Options\".platformName","Windows 11");
            environmentVariables.setProperty("environment","myenv");

            assertThat(LambdaTestConfiguration.isActiveFor(environmentVariables)).isTrue();
        }

        @Test
        @DisplayName("When the remote URL points to a lambdatest server")
        public void remoteUrlPointsToLambdatest() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();

            environmentVariables.setProperty("webdriver.driver","remote");
            environmentVariables.setProperty("webdriver.remote.url","user:key@hub.lambdatest.com/wd/hub");

            assertThat(LambdaTestConfiguration.isActiveFor(environmentVariables)).isTrue();
        }

        @Test
        @DisplayName("When the remote URL points to a lambdatest server in an active environment")
        public void remoteUrlPointsToLambdatestInEnv() {
            MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();


            environmentVariables.setProperty("environments.myenv.webdriver.driver","remote");
            environmentVariables.setProperty("environments.myenv.webdriver.remote.url","user:key@hub.lambdatest.com/wd/hub");
            environmentVariables.setProperty("environment","myenv");

            assertThat(LambdaTestConfiguration.isActiveFor(environmentVariables)).isTrue();
        }
    }

}
