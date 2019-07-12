package net.serenitybdd.core.webdriver.enhancers;

import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public interface CustomDriverEnhancer {
    void apply(EnvironmentVariables environmentVariables,
                        WebDriver driver);
}
