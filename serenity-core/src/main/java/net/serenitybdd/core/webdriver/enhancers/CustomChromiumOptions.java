package net.serenitybdd.core.webdriver.enhancers;

import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.chromium.ChromiumOptions;

public interface CustomChromiumOptions {
    void apply(EnvironmentVariables environmentVariables, ChromiumOptions<?> options);
}
