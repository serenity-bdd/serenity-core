package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import static net.thucydides.core.ThucydidesSystemProperty.BROWSERSTACK_URL;
import static net.thucydides.core.ThucydidesSystemProperty.SAUCELABS_URL;

/**
 * A Remote Driver using Saucelabs or Browserstack (for remote web tesing), or Selenium Grid.
 * This class should not be used for Appium testing, as Appium is already a remote driver.
 */
public class RemoteDriverProvider implements DriverProvider {

    enum RemoteDriverType { SAUCELABS, BROWSERSTACK, DEFAULT}

    private final FixtureProviderService fixtureProviderService;

    public RemoteDriverProvider(FixtureProviderService fixtureProviderService) {
        this.fixtureProviderService = fixtureProviderService;
    }


    private Map<RemoteDriverType,RemoteDriverBuilder> driverBuildersFor(EnvironmentVariables environmentVariables) {
        Map<RemoteDriverType,RemoteDriverBuilder> driverBuilders = new HashMap();

        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);

        DriverCapabilities remoteDriverCapabilities = new DriverCapabilities(environmentVariables, enhancer);

        driverBuilders.put(RemoteDriverType.SAUCELABS, new SaucelabsDriverBuilder(environmentVariables, remoteDriverCapabilities));
        driverBuilders.put(RemoteDriverType.BROWSERSTACK, new BrowserStackDriverBuilder(environmentVariables, remoteDriverCapabilities));
        driverBuilders.put(RemoteDriverType.DEFAULT, new DefaultRemoteDriver(environmentVariables, remoteDriverCapabilities));


        return driverBuilders;
    }

    @Override
    public WebDriver newInstance(String options, EnvironmentVariables environmentVariables) throws MalformedURLException  {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return RemoteWebdriverStub.from(environmentVariables);
        }

        WebDriver driver = driverBuildersFor(environmentVariables).get(remoteDriverType(environmentVariables)).buildWithOptions(options);

        return new Augmenter().augment(driver);
    }

    private RemoteDriverType remoteDriverType(EnvironmentVariables environmentVariables) {

        if (saucelabsUrlIsDefined(environmentVariables)) {
            return RemoteDriverType.SAUCELABS;
//        } else if (browserStackUrlIsDefined(environmentVariables)){
//            return RemoteDriverType.BROWSERSTACK;
        } else {
            return RemoteDriverType.DEFAULT;
        }
    }

    private boolean saucelabsUrlIsDefined(EnvironmentVariables environmentVariables) {
        return StringUtils.isNotEmpty(SAUCELABS_URL.from(environmentVariables));
    }

    private boolean browserStackUrlIsDefined(EnvironmentVariables environmentVariables) {
        return StringUtils.isNotEmpty(BROWSERSTACK_URL.from(environmentVariables));
    }
}
