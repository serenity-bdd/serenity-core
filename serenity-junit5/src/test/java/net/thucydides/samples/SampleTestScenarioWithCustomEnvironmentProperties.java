package net.thucydides.samples;

import net.serenitybdd.core.annotations.environment.EnvironmentProperty;
import net.serenitybdd.junit5.SerenityAfterEachCallback;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.thucydides.model.util.EnvironmentVariables;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SerenityAfterEachCallback.class)
@ExtendWith(SerenityJUnit5Extension.class)
public class SampleTestScenarioWithCustomEnvironmentProperties {

    EnvironmentVariables environmentVariables;

    @EnvironmentProperty(name = "color",value = "red")
    @Test
    public void shouldBeAbleToOverrideEnvironmentVariables() {
        assertThat(environmentVariables.getProperty("color")).isEqualTo("red");
    }

    @Test
    public void shouldResetEnvironmentVariablesBetweenTests() {
        assertThat(environmentVariables.getProperty("color")).isNull();
    }

    @EnvironmentProperty(name = "color",value = "blue")
    @EnvironmentProperty(name = "flavor",value = "vanilla")
    @Test
    public void shouldBeAbleToSetMultipleEnvironmentVariables() {
        assertThat(environmentVariables.getProperty("color")).isEqualTo("blue");
        assertThat(environmentVariables.getProperty("flavor")).isEqualTo("vanilla");
    }

    @EnvironmentProperty(name = "user.name",value = "Foo")
    @Test
    public void shouldBeAbleToOverrideDefaultProperties() {
        assertThat(environmentVariables.getProperty("user.name")).isEqualTo("Foo");
    }
}
