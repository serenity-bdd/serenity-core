package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.ProvidedDriverConfiguration;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

public class ProvidedDriverProvider implements DriverProvider {

    public ProvidedDriverProvider() {
    }

    @Override
    public WebDriver newInstance(String options, EnvironmentVariables environmentVariables) {
        if (StepEventBus.getParallelEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }

        ProvidedDriverConfiguration sourceConfig = new ProvidedDriverConfiguration(environmentVariables);
        try {
            return sourceConfig.getDriverSource().newDriver();
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate the custom webdriver provider of type " + sourceConfig.getDriverName(), e);
        }
    }
}
