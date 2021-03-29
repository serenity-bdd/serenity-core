package serenitycore.net.serenitybdd.core.webdriver.enhancers;

import serenitymodel.net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.chrome.ChromeOptions;

public interface CustomChromeOptions {
    void apply(EnvironmentVariables environmentVariables, ChromeOptions options);
}
