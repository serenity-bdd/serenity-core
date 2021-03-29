package serenitycore.net.serenitybdd.core.webdriver.enhancers;

import serenitymodel.net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

public interface CustomDriverEnhancer {
    void apply(EnvironmentVariables environmentVariables,
                        WebDriver driver);
}
