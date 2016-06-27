package net.thucydides.core.webdriver.strategies;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class FirefoxDriverBuilder implements DriverBuilder {

    private final EnvironmentVariables environmentVariables;
    private final CapabilityEnhancer enhancer;
    private final DriverCapabilityRecord driverProperties;

    public FirefoxDriverBuilder(EnvironmentVariables environmentVariables, CapabilityEnhancer enhancer) {
        this.environmentVariables = environmentVariables;
        this.enhancer = enhancer;
        this.driverProperties = Injectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    public WebDriver newInstance() {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }
        DesiredCapabilities capabilities = new FirefoxDriverCapabilities(environmentVariables).getCapabilities();
        if (geckoDriverIsOnTheClasspath()) {
            capabilities.setCapability("marionette", true);
        }
        FirefoxDriver driver = new FirefoxDriver(capabilities);

        driverProperties.registerCapabilities("firefox", driver.getCapabilities());
        return driver;
    }

    private boolean geckoDriverIsOnTheClasspath() {
        try {
            Runtime.getRuntime().exec("geckodriver --help");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
