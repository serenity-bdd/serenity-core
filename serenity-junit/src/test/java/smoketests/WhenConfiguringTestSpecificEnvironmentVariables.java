package smoketests;

import net.serenitybdd.core.annotations.environment.EnvironmentProperty;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.util.EnvironmentVariables;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class WhenConfiguringTestSpecificEnvironmentVariables {

    @EnvironmentProperty(name = "color",value = "red")
    @Test
    public void shouldBeAbleToOverrideEnvironmentVariables() {
        assertThat(SystemEnvironmentVariables.currentEnvironmentVariables().getProperty("color")).isEqualTo("red");
    }

    @EnvironmentProperty(name = "color",value = "red")
    @Test
    public void shouldBeAbleToResetEnvironmentVariables() {
        assertThat(SystemEnvironmentVariables.currentEnvironmentVariables().getProperty("color")).isEqualTo("red");
        SystemEnvironmentVariables.currentEnvironment().reset();
        assertThat(SystemEnvironmentVariables.currentEnvironmentVariables().getProperty("color")).isNull();
    }

    @Test
    public void shouldResetEnvironmentVariablesBetweenTests() {
        assertThat(SystemEnvironmentVariables.currentEnvironmentVariables().getProperty("color")).isNull();
    }

    @EnvironmentProperty(name = "color",value = "blue")
    @EnvironmentProperty(name = "flavor",value = "vanilla")
    @Test
    public void shouldBeAbleToSetMultipleEnvironmentVariables() {
        assertThat(SystemEnvironmentVariables.currentEnvironmentVariables().getProperty("color")).isEqualTo("blue");
        assertThat(SystemEnvironmentVariables.currentEnvironmentVariables().getProperty("flavor")).isEqualTo("vanilla");
    }

    @EnvironmentProperty(name = "user.name",value = "Foo")
    @Test
    public void shouldBeAbleToOverrideDefaultProperties() {
        assertThat(SystemEnvironmentVariables.currentEnvironmentVariables().getProperty("user.name")).isEqualTo("Foo");
    }
}
