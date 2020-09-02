package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.di.WebDriverInjectors;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class HtmlDriverProvider implements DriverProvider {

    private final DriverCapabilityRecord driverProperties;

    private final FixtureProviderService fixtureProviderService;

    public HtmlDriverProvider(FixtureProviderService fixtureProviderService) {
        this.fixtureProviderService = fixtureProviderService;
        this.driverProperties = WebDriverInjectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    public WebDriver newInstance(String options, EnvironmentVariables environmentVariables) {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }
        DesiredCapabilities capabilities = DesiredCapabilities.htmlUnit();
        capabilities.setJavascriptEnabled(true);

        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);

        DesiredCapabilities enhancedCapabilities = enhancer.enhanced(capabilities, SupportedWebDriver.CHROME);

        HtmlUnitDriver driver = new HtmlUnitDriver(enhancedCapabilities);
        driverProperties.registerCapabilities("htmlunit", capabilitiesToProperties(enhancedCapabilities));
        return driver;
    }

}
