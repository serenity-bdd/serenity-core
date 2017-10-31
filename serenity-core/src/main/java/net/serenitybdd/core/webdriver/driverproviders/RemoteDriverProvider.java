package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
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

    private final EnvironmentVariables environmentVariables;

    private final DriverCapabilities remoteDriverCapabilities;

    enum RemoteDriverType { SAUCELABS, BROWSERSTACK, DEFAULT}

    private final Map<RemoteDriverType,RemoteDriverBuilder> DRIVER_BUILDERS = new HashMap<>();

    public RemoteDriverProvider(EnvironmentVariables environmentVariables, CapabilityEnhancer enhancer) {
        this.environmentVariables = environmentVariables;
        this.remoteDriverCapabilities = new DriverCapabilities(environmentVariables,enhancer);

        DRIVER_BUILDERS.put(RemoteDriverType.SAUCELABS, new SaucelabsDriverBuilder(environmentVariables, remoteDriverCapabilities));
        DRIVER_BUILDERS.put(RemoteDriverType.BROWSERSTACK, new BrowserStackDriverBuilder(environmentVariables, remoteDriverCapabilities));
        DRIVER_BUILDERS.put(RemoteDriverType.DEFAULT, new DefaultRemoteDriver(environmentVariables, remoteDriverCapabilities));
    }

    @Override
    public WebDriver newInstance(String options) throws MalformedURLException {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return RemoteWebdriverStub.from(environmentVariables);
        }

        WebDriver driver = DRIVER_BUILDERS.get(remoteDriverType()).buildWithOptions(options);

        return new Augmenter().augment(driver);
    }

    private RemoteDriverType remoteDriverType() {

        if (saucelabsUrlIsDefined()) {
            return RemoteDriverType.SAUCELABS;
        } else if (browserStackUrlIsDefined()){
            return RemoteDriverType.BROWSERSTACK;
        } else {
            return RemoteDriverType.DEFAULT;
        }
    }

    private boolean saucelabsUrlIsDefined() {
        return StringUtils.isNotEmpty(SAUCELABS_URL.from(environmentVariables));
    }

    private boolean browserStackUrlIsDefined() {
        return StringUtils.isNotEmpty(BROWSERSTACK_URL.from(environmentVariables));
    }
}
