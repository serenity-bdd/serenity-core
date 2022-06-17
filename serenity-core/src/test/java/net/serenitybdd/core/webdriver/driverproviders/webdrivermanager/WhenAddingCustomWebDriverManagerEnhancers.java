package net.serenitybdd.core.webdriver.driverproviders.webdrivermanager;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.environment.MockEnvironmentVariables;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenAddingCustomWebDriverManagerEnhancers {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    @Test
    public void weCanEnhanceAWebDriverManagerSetupByImplementingAnEnhancer() {

        WebDriverManager webDriverManager = WebDriverManager.chromedriver();
        WebDriverManagerSetup.usingEnvironmentVariables(environmentVariables).enhance(webDriverManager);

        assertThat(SampleWebDriverManagerEnhancer.WAS_ENHANCED).isTrue();
    }
}
