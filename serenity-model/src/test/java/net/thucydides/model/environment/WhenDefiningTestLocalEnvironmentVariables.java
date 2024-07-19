package net.thucydides.model.environment;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenDefiningTestLocalEnvironmentVariables {

    @Test
    public void shouldBeAbleToDefineVariablesForIndividualTests() {
        TestLocalEnvironmentVariables.setProperty("some.variable","red");
        assertThat(TestLocalEnvironmentVariables.getProperty("some.variable")).isEqualTo("red");
    }

    @Test
    public void fetchAllTheCurrentTestLocalProperties() {
        TestLocalEnvironmentVariables.setProperty("some.variable","red");
        TestLocalEnvironmentVariables.setProperty("some.other.variable","blue");

        assertThat(TestLocalEnvironmentVariables.getProperties()).hasSize(2);
    }

    @Test
    public void clearAllTheCurrentTestLocalProperties() {
        TestLocalEnvironmentVariables.setProperty("some.variable","red");
        TestLocalEnvironmentVariables.setProperty("some.other.variable","blue");

        TestLocalEnvironmentVariables.clear();

        assertThat(TestLocalEnvironmentVariables.getProperties()).hasSize(0);
    }

    @Test
    public void testLocalVariablesShouldBeClearedBetweenTests() {
        assertThat(TestLocalEnvironmentVariables.getProperty("some.variable")).isNull();
    }
}
