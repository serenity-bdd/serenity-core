package net.serenitybdd.core.webdriver.integration;

import com.google.common.io.Resources;
import net.serenitybdd.core.webdriver.driverproviders.FirefoxDriverProvider;
import net.thucydides.core.fixtureservices.ClasspathFixtureProviderService;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenInstantiatingAFirefoxDriver {

    FixtureProviderService fixtureProviderService;
    FirefoxDriverProvider driverProvider;
    WebDriver driver;

    @Before
    public void setupProvider() {
        fixtureProviderService = new ClasspathFixtureProviderService();
        driverProvider = new FirefoxDriverProvider(fixtureProviderService);
    }

    @After
    public void cleanup() {
        driver.quit();
    }

    @Test
    public void createASimpleDriver() {
        Path simpleConfigFile = configFileCalled("sample-conf-files/firefox/simple.conf");
        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.createEnvironmentVariables(simpleConfigFile);

        driver = driverProvider.newInstance("", environmentVariables);
        driver.get("https://example.com/");

        assertThat(driver.getTitle()).isEqualTo("Example Domain");
    }

    @NotNull
    private Path configFileCalled(String filename) {
        return Paths.get(Resources.getResource(filename).getPath());
    }

}
