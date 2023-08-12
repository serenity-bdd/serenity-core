package net.serenitybdd.core.webdriver.enhancers;

import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

public interface CustomDriverEnhancer {
    void apply(EnvironmentVariables environmentVariables,  WebDriver driver);
}
