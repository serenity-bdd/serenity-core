package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.buildinfo.*;
import net.serenitybdd.core.di.*;
import net.thucydides.core.fixtureservices.*;
import net.thucydides.core.steps.*;
import net.thucydides.core.util.*;
import net.thucydides.core.webdriver.*;
import net.thucydides.core.webdriver.stubs.*;
import org.openqa.selenium.*;
import org.openqa.selenium.htmlunit.*;
import org.openqa.selenium.remote.*;

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

        DesiredCapabilities enhancedCapabilities = enhancer.enhanced(capabilities);

        HtmlUnitDriver driver = new HtmlUnitDriver(enhancedCapabilities);
        driverProperties.registerCapabilities("htmlunit", capabilitiesToProperties(enhancedCapabilities));
        return driver;
    }

}
