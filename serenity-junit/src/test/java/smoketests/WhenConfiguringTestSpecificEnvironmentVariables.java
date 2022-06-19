package smoketests;

import net.serenitybdd.core.annotations.environment.EnvironmentProperty;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.util.EnvironmentVariables;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class WhenConfiguringTestSpecificEnvironmentVariables {
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
