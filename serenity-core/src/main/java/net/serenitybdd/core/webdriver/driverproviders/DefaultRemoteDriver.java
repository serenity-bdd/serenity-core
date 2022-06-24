package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.webdriver.driverproviders.cache.PreScenarioFixtures;
import net.serenitybdd.core.webdriver.enhancers.ProvidesRemoteWebdriverUrl;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.util.EnvironmentVariables;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import static net.thucydides.core.webdriver.WebDriverFactory.getDriverFrom;

class DefaultRemoteDriver extends RemoteDriverBuilder {
    private final DriverCapabilities remoteDriverCapabilities;

    DefaultRemoteDriver(EnvironmentVariables environmentVariables, DriverCapabilities remoteDriverCapabilities) {
        super(environmentVariables);
        this.remoteDriverCapabilities = remoteDriverCapabilities;
    }

    WebDriver buildWithOptions(String options) throws MalformedURLException {
        String remoteUrl = ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL.from(environmentVariables);
        if (remoteUrl == null) {
            EnvironmentVariables environmentVariables = SystemEnvironmentVariables.currentEnvironmentVariables();
            remoteUrl = getRemoteUrlFromFixtureClasses(environmentVariables);
        }
        if (remoteUrl == null) {
            throw new InvalidArgumentException("A webdriver.remote.url property must be defined when using a Remote driver.");
        }
        Capabilities capabilities = buildRemoteCapabilities(options);
        return newRemoteDriver(new URL(remoteUrl), capabilities, options);
    }

    @Nullable
    private String getRemoteUrlFromFixtureClasses(EnvironmentVariables environmentVariables) {
        return PreScenarioFixtures.executeBeforeAWebdriverScenario().stream()
                .filter(fixture -> fixture.isActivated(environmentVariables))
                .filter(fixture -> fixture instanceof ProvidesRemoteWebdriverUrl)
                .map(fixture -> (ProvidesRemoteWebdriverUrl) fixture)
                .filter(fixture -> fixture.remoteUrlDefinedIn(environmentVariables).isPresent())
                .map(fixture -> fixture.remoteUrlDefinedIn(environmentVariables).orElse(null))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private Capabilities buildRemoteCapabilities(String options) {
        String driver = ThucydidesSystemProperty.WEBDRIVER_REMOTE_DRIVER.from(environmentVariables);
        if (driver == null) {
            driver = getDriverFrom(environmentVariables);
        }
        return remoteDriverCapabilities.forDriver(driver, options);
    }

}
