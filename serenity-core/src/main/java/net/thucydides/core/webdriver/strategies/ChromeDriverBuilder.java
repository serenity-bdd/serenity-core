package net.thucydides.core.webdriver.strategies;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class ChromeDriverBuilder implements DriverBuilder {

    private final EnvironmentVariables environmentVariables;
    private final CapabilityEnhancer enhancer;
    private final DriverCapabilityRecord driverProperties;

    public ChromeDriverBuilder(EnvironmentVariables environmentVariables, CapabilityEnhancer enhancer) {
        this.environmentVariables = environmentVariables;
        this.enhancer = enhancer;
        this.driverProperties = Injectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    public WebDriver newInstance() {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }
        DesiredCapabilities capabilities = new ChromeDriverCapabilities(environmentVariables).getCapabilities();
        updateChromePathIfSpecifiedIn(environmentVariables);

        ChromeDriver driver = new ChromeDriver(enhancer.enhanced(capabilities));
        driverProperties.registerCapabilities("chrome", driver.getCapabilities());
        return driver;
    }

    private void updateChromePathIfSpecifiedIn(EnvironmentVariables environmentVariables) {
        String environmentDefinedChromeDriverPath = environmentVariables.getProperty(ThucydidesSystemProperty.WEBDRIVER_CHROME_DRIVER);
        if (StringUtils.isNotEmpty(environmentDefinedChromeDriverPath)) {
            System.setProperty(ThucydidesSystemProperty.WEBDRIVER_CHROME_DRIVER.toString(), environmentDefinedChromeDriverPath);
        }
    }
}
