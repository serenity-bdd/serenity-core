package net.serenitybdd.core.webdriver.driverproviders;

import com.google.common.base.Splitter;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.webdriver.driverproviders.cache.PreScenarioFixtures;
import net.serenitybdd.core.webdriver.enhancers.ProvidesRemoteWebdriverUrl;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.TestContext;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.capabilities.W3CCapabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.thucydides.model.ThucydidesSystemProperty.WEBDRIVER_DRIVER;
import static net.thucydides.model.ThucydidesSystemProperty.WEBDRIVER_REMOTE_DRIVER;

/**
 * A Remote Driver using services like Browserstack, LambdaTest or SauceLabs (for remote web testing), or Selenium Grid.
 * This class should not be used for Appium testing, as Appium is already a remote driver.
 */
public class RemoteDriverProvider implements DriverProvider {

    public static final String MISSING_REMOTE_DRIVER_PROPERTY = "Remote driver not specified";

    private final FixtureProviderService fixtureProviderService;

    public RemoteDriverProvider(FixtureProviderService fixtureProviderService) {
        this.fixtureProviderService = fixtureProviderService;
    }

    @Override
    public WebDriver newInstance(String options, EnvironmentVariables environmentVariables) {
        if (StepEventBus.getParallelEventBus().webdriverCallsAreSuspended()) {
            return RemoteWebdriverStub.from(environmentVariables);
        } else {
            //
            // The remote url is defined in serenity.conf or can be defined in a class that implements ProvidesRemoteWebdriverUrl
            //
            URL remoteUrl = getRemoteUrlFrom(environmentVariables);
            //
            // The name of the actual driver to be run remotely, which is defined either in webdriver.remote.driver or in the W3C capabilities
            //
            String driverName = getRemoteDriverNameFrom(environmentVariables);
            //
            // The W3C capabilities defined in the webdriver.capabilities section of serenity.conf
            //
            MutableCapabilities capabilities = W3CCapabilities.definedIn(environmentVariables)
                    .withPrefix("webdriver.capabilities")
                    .forDriver(SupportedWebDriver.getDriverTypeFor(driverName));

            //
            // Add options if possible
            //
            if (capabilities instanceof ChromiumOptions<?>) {
                ((ChromiumOptions<?>) capabilities).addArguments(argumentsIn(options));
            } else if (capabilities instanceof FirefoxOptions) {
                ((FirefoxOptions) capabilities).addArguments(argumentsIn(options));
            }
            //
            // Call any FixtureService and BeforeAWebdriverScenario classes
            EnhanceCapabilitiesWithFixtures.using(fixtureProviderService).into(capabilities);
            AddCustomDriverCapabilities.from(environmentVariables)
                    .withTestDetails(SupportedWebDriver.getDriverTypeFor(driverName), StepEventBus.getParallelEventBus().getBaseStepListener().getCurrentTestOutcome())
                    .to(capabilities);
            //
            // Record browser and platform
            //
            TestContext.forTheCurrentTest().recordBrowserAndPlatformConfiguration(capabilities);

            return new RemoteWebDriver(remoteUrl, capabilities);
        }
    }

    private URL getRemoteUrlFrom(EnvironmentVariables environmentVariables) {

        String remoteUrl = null;

        try {
            Optional<String> environmentDefinedRemoteUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL);
            if (environmentDefinedRemoteUrl.isPresent()) {
                remoteUrl = environmentDefinedRemoteUrl.get();
            } else {
                Optional<String> remoteUrlDefinedInFixtureClasses = getRemoteUrlFromFixtureClasses(environmentVariables);
                if (remoteUrlDefinedInFixtureClasses.isPresent()) {
                    remoteUrl = remoteUrlDefinedInFixtureClasses.get();
                }
            }
            if (remoteUrl == null) {
                throw new RemoteDriverConfigurationError("A webdriver.remote.url property must be defined when using a Remote driver.");
            }
            return new URL(remoteUrl);
        } catch (MalformedURLException e) {
            throw new RemoteDriverConfigurationError("Incorrectly formed webdriver.remote.url property: " + remoteUrl, e);
        }
    }

    private String getRemoteDriverNameFrom(EnvironmentVariables environmentVariables) {
        return EnvironmentSpecificConfiguration
                .from(environmentVariables)
                .getOptionalProperty("webdriver.capabilities.browserName")
                .orElseGet(() ->
                        EnvironmentSpecificConfiguration
                                .from(environmentVariables)
                                .getOptionalProperty(WEBDRIVER_REMOTE_DRIVER, WEBDRIVER_DRIVER)
                                .orElseThrow(() -> new RemoteDriverConfigurationError(MISSING_REMOTE_DRIVER_PROPERTY))
                );
    }

    private List<String> argumentsIn(String options) {
        return Splitter.on(";").omitEmptyStrings().splitToList(options);
    }

    private Optional<String> getRemoteUrlFromFixtureClasses(EnvironmentVariables environmentVariables) {
        return PreScenarioFixtures.executeBeforeAWebdriverScenario().stream()
                .filter(fixture -> fixture.isActivated(environmentVariables))
                .filter(fixture -> fixture instanceof ProvidesRemoteWebdriverUrl)
                .map(fixture -> (ProvidesRemoteWebdriverUrl) fixture)
                .filter(fixture -> fixture.remoteUrlDefinedIn(environmentVariables).isPresent())
                .map(fixture -> fixture.remoteUrlDefinedIn(environmentVariables).orElse(null))
                .filter(Objects::nonNull)
                .findFirst();
    }
}
