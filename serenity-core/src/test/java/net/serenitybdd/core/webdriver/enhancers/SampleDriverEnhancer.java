package net.serenitybdd.core.webdriver.enhancers;

import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class SampleDriverEnhancer implements CustomDriverEnhancer {

    @Override
    public void apply(EnvironmentVariables environmentVariables, WebDriver driver) {
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(10));
    }
}