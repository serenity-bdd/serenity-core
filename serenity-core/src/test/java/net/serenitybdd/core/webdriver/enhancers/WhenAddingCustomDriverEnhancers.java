package net.serenitybdd.core.webdriver.enhancers;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.core.webdriver.driverproviders.EnhanceDriver;
import net.thucydides.core.environment.MockEnvironmentVariables;
import net.thucydides.core.util.EnvironmentVariables;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenAddingCustomDriverEnhancers {

    @Before
    public void setupWebDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @Test
    public void customEnhancersCanAddExtraCapabilitiesWhenADriverIsCreated() {

        // Given
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("serenity.extension.packages","net.serenitybdd.core.webdriver.enhancers");
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        WebDriver driver = new ChromeDriver(options);

        // When
        EnhanceDriver.from(environmentVariables).to(driver);

        // Then
        assertThat(driver.manage().timeouts().getScriptTimeout()).isEqualTo(Duration.ofSeconds(10));

        driver.close();
    }
}
