package net.serenitybdd.core.webdriver.enhancers;

import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.chromium.ChromiumOptions;

/**
 * Implement this interface to add your own custom capabilities during driver creation.
 */
public interface CustomChromiumOptions {
    void apply(EnvironmentVariables environmentVariables, ChromiumOptions<?> options);
}
