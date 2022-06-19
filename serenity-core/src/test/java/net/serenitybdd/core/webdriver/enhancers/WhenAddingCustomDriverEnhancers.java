package net.serenitybdd.core.webdriver.enhancers;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.core.webdriver.driverproviders.EnhanceDriver;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.environment.MockEnvironmentVariables;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenAddingCustomDriverEnhancers {

    @Test
    public void customEnhancersCanAddExtraCapabilitiesWhenADriverIsCreated() {


        // Given
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("serenity.extension.packages","net.serenitybdd.core.webdriver.enhancers");
        WebDriver driver = new HtmlUnitDriver();

        // When
        EnhanceDriver.from(environmentVariables).to(driver);

        // Then
        assertThat(driver.manage().timeouts().getScriptTimeout()).isEqualTo(Duration.ofSeconds(10));

        driver.close();
    }
}
