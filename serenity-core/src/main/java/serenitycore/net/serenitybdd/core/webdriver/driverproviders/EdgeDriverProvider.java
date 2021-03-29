package serenitycore.net.serenitybdd.core.webdriver.driverproviders;

import io.github.bonigarcia.wdm.WebDriverManager;
import serenitymodel.net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import serenitycore.net.serenitybdd.core.di.WebDriverInjectors;
import serenitycore.net.serenitybdd.core.webdriver.servicepools.DriverServicePool;
import serenitycore.net.serenitybdd.core.webdriver.servicepools.EdgeServicePool;
import serenitycore.net.thucydides.core.fixtureservices.FixtureProviderService;
import serenitycore.net.thucydides.core.steps.StepEventBus;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;
import serenitycore.net.thucydides.core.webdriver.CapabilityEnhancer;
import serenitycore.net.thucydides.core.webdriver.SupportedWebDriver;
import serenitycore.net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

public class EdgeDriverProvider implements DriverProvider {

    private final DriverCapabilityRecord driverProperties;

    private final DriverServicePool driverServicePool = new EdgeServicePool();

    private final FixtureProviderService fixtureProviderService;

    public EdgeDriverProvider(FixtureProviderService fixtureProviderService) {
        this.fixtureProviderService = fixtureProviderService;
        this.driverProperties = WebDriverInjectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    public WebDriver newInstance(String options, EnvironmentVariables environmentVariables) {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }

        if(isDriverAutomaticallyDownloaded(environmentVariables)) {
            WebDriverManager.edgedriver().setup();
        }

        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);
        DesiredCapabilities desiredCapabilities = enhancer.enhanced(
            new DesiredCapabilities(new EdgeOptions()),
            SupportedWebDriver.EDGE);

        driverProperties.registerCapabilities("edge", capabilitiesToProperties(desiredCapabilities));

        SetProxyConfiguration.from(environmentVariables).in(desiredCapabilities);
        AddLoggingPreferences.from(environmentVariables).to(desiredCapabilities);

        return ProvideNewDriver.withConfiguration(environmentVariables,
                desiredCapabilities,
                driverServicePool,
                DriverServicePool::newDriver,
                (pool, caps) -> new EdgeDriver(caps)
        );
    }
}
