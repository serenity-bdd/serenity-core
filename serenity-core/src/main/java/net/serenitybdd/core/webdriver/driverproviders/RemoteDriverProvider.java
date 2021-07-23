package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.reports.remoteTesting.ASaucelabsConfiguration;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;

import java.net.MalformedURLException;

/**
 * A Remote Driver using Saucelabs or Browserstack (for remote web tesing), or Selenium Grid.
 * This class should not be used for Appium testing, as Appium is already a remote driver.
 */
public class RemoteDriverProvider implements DriverProvider {

    private final FixtureProviderService fixtureProviderService;

    public RemoteDriverProvider(FixtureProviderService fixtureProviderService) {
        this.fixtureProviderService = fixtureProviderService;
    }

    @Override
    public WebDriver newInstance(String options, EnvironmentVariables environmentVariables) throws MalformedURLException {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return RemoteWebdriverStub.from(environmentVariables);
        } else {
            CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);
            DriverCapabilities remoteDriverCapabilities = new DriverCapabilities(environmentVariables, enhancer);

            WebDriver driver = new DefaultRemoteDriver(environmentVariables, remoteDriverCapabilities).buildWithOptions(options);
            return new Augmenter().augment(driver);
        }
    }
}
