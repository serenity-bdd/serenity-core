package net.serenitybdd.core.webdriver.integration;

import com.google.common.io.Resources;
import net.serenitybdd.core.webdriver.driverproviders.ChromeDriverProvider;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.core.fixtureservices.ClasspathFixtureProviderService;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.model.util.EnvironmentVariables;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenInstantiatingAChromeDriver {

    FixtureProviderService fixtureProviderService;
    ChromeDriverProvider chromeDriverProvider;
    WebDriver driver;

    @Before
    public void setupProvider() {
        fixtureProviderService = new ClasspathFixtureProviderService();
        chromeDriverProvider = new ChromeDriverProvider(fixtureProviderService);
    }

    @After
    public void cleanup() {
        driver.quit();
    }

    @Test
    public void createASimpleDriver() {
        Path simpleConfigFile = configFileCalled("simple.conf");
        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.createEnvironmentVariables(simpleConfigFile);

        driver = chromeDriverProvider.newInstance("", environmentVariables);
        driver.get("https://example.com/");
        assertThat(driver.getTitle()).isEqualTo("Example Domain");
    }

    @Test
    public void createADriverWithMoreOptions() {
        Path simpleConfigFile = configFileCalled("detailed.conf");
        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.createEnvironmentVariables(simpleConfigFile);

        driver = chromeDriverProvider.newInstance("", environmentVariables);
        driver.get("https://example.com/");
        assertThat(driver.getTitle()).isEqualTo("Example Domain");
    }


    @Test
    public void createADriverUsingAnExtension() {
        Path simpleConfigFile = configFileCalled("with-extension.conf");
        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.createEnvironmentVariables(simpleConfigFile);

        driver = chromeDriverProvider.newInstance("", environmentVariables);
        driver.get("https://example.com/");
        assertThat(driver.getTitle()).isEqualTo("Example Domain");
    }

    @NotNull
    private Path configFileCalled(String filename) {
        return Paths.get(Resources.getResource("sample-conf-files/chrome/" + filename).getPath());
    }

}
