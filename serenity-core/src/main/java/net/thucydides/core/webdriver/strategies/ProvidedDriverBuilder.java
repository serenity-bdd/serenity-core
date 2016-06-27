package net.thucydides.core.webdriver.strategies;

import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.ProvidedDriverConfiguration;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;

public class ProvidedDriverBuilder implements DriverBuilder {

    private final EnvironmentVariables environmentVariables;

    public ProvidedDriverBuilder(EnvironmentVariables environmentVariables, CapabilityEnhancer enhancer) {
        this.environmentVariables = environmentVariables;
    }

    @Override
    public WebDriver newInstance() {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
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
