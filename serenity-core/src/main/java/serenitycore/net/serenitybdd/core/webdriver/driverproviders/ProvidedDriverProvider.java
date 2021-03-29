package serenitycore.net.serenitybdd.core.webdriver.driverproviders;

import serenitycore.net.thucydides.core.steps.StepEventBus;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;
import serenitycore.net.thucydides.core.webdriver.ProvidedDriverConfiguration;
import serenitycore.net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;

public class ProvidedDriverProvider implements DriverProvider {

    public ProvidedDriverProvider() {
    }

    @Override
    public WebDriver newInstance(String options, EnvironmentVariables environmentVariables) {
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
