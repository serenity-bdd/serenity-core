package net.serenitybdd.core.webdriver.integration;

import net.serenitybdd.core.webdriver.enhancers.CustomChromiumOptions;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.chromium.ChromiumOptions;

public class SampleExtension implements CustomChromiumOptions {
    @Override
    public void apply(EnvironmentVariables environmentVariables, ChromiumOptions<?> options) {
        options.setHeadless(true);
    }
}
