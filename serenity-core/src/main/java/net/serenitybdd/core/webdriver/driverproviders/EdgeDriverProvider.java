package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.di.WebDriverInjectors;
import net.serenitybdd.core.webdriver.driverproviders.webdrivermanager.WebDriverManagerSetup;
import net.serenitybdd.core.webdriver.servicepools.DriverServicePool;
import net.serenitybdd.core.webdriver.servicepools.EdgeServicePool;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.capabilities.BrowserPreferences;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;

import java.util.List;
import java.util.Map;

public class EdgeDriverProvider implements DriverProvider {

    private final DriverCapabilityRecord driverProperties;
    private final EnvironmentVariables environmentVariables;
    private final DriverServicePool<EdgeDriverService>  driverServicePool = new EdgeServicePool();

    private final FixtureProviderService fixtureProviderService;

    public EdgeDriverProvider(FixtureProviderService fixtureProviderService) {
        this.fixtureProviderService = fixtureProviderService;
        this.driverProperties = WebDriverInjectors.getInjector().getInstance(DriverCapabilityRecord.class);
        this.environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
    }

    @Override
    public WebDriver newInstance(String options, EnvironmentVariables environmentVariables) {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }

        if(isDriverAutomaticallyDownloaded(environmentVariables)) {
            WebDriverManagerSetup.usingEnvironmentVariables(environmentVariables).forEdge();
        }

        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);
        MutableCapabilities desiredCapabilities = enhancer.enhanced(
                new EdgeDriverCapabilities(environmentVariables, options).getCapabilities(),
                SupportedWebDriver.EDGE);

        driverProperties.registerCapabilities("edge", capabilitiesToProperties(desiredCapabilities));

        SetProxyConfiguration.from(environmentVariables).in(desiredCapabilities);
        AddLoggingPreferences.from(environmentVariables).to(desiredCapabilities);

        EdgeOptions edgeOptions = new EdgeOptions();
        List<String> args = DriverArgs.fromValue(options);
        edgeOptions.addArguments(args);
        if (args.contains("headless") || args.contains("--headless")) {
            edgeOptions.setHeadless(true);
        }

        addPreferencesTo(edgeOptions);
        EdgeOptions enhancedOptions = edgeOptions.merge(desiredCapabilities);

        return ProvideNewDriver.withConfiguration(environmentVariables,
                desiredCapabilities,
                driverServicePool,
                DriverServicePool::newDriver,
                (pool, caps) -> new EdgeDriver(enhancedOptions)
        );
    }

    public static Map<String, Object> optionsConfiguredIn(EnvironmentVariables environmentVariables) {
        Map<String, Object> chromePreferences = BrowserPreferences.startingWith("edge.options.").from(environmentVariables);
        return SanitisedBrowserPreferences.cleanUpPathsIn(chromePreferences);
    }

    private void addPreferencesTo(EdgeOptions options) {
        Map<String, Object> optionValues = optionsConfiguredIn(environmentVariables);
        optionValues.forEach(options::setCapability);
    }
}
