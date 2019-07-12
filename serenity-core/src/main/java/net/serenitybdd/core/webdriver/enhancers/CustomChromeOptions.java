package net.serenitybdd.core.webdriver.enhancers;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

public interface CustomChromeOptions {
    void apply(EnvironmentVariables environmentVariables, ChromeOptions options);
}
