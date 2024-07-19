package net.serenitybdd.core.webdriver.integration;

import com.google.common.io.Resources;
import net.serenitybdd.core.webdriver.driverproviders.ChromeDriverProvider;
import net.serenitybdd.core.webdriver.driverproviders.EdgeDriverProvider;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.core.fixtureservices.ClasspathFixtureProviderService;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.model.util.EnvironmentVariables;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenInstantiatingAnEdgeDriver {

    FixtureProviderService fixtureProviderService;
    EdgeDriverProvider driverProvider;
    WebDriver driver;

    @Before
    public void setupProvider() {
        fixtureProviderService = new ClasspathFixtureProviderService();
        driverProvider = new EdgeDriverProvider(fixtureProviderService);
    }

    @After
    public void cleanup() {
        driver.quit();
    }

    @Test
    public void createASimpleDriver() {
        Path simpleConfigFile = configFileCalled("simple-edge-config.conf");
        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.createEnvironmentVariables(simpleConfigFile);

        driver = driverProvider.newInstance("", environmentVariables);
        driver.get("https://example.com/");

        assertThat(driver.getTitle()).isEqualTo("Example Domain");
    }

    @NotNull
    private Path configFileCalled(String filename) {
        return Paths.get(Resources.getResource("sample-conf-files/edge/" + filename).getPath());
    }

}
