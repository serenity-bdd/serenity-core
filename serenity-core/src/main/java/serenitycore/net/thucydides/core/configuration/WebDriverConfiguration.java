package serenitycore.net.thucydides.core.configuration;

import com.google.inject.Inject;
import serenitymodel.net.thucydides.core.configuration.SystemPropertiesConfiguration;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;

import java.util.Optional;
import serenitycore.net.thucydides.core.webdriver.DriverConfiguration;
import serenitycore.net.thucydides.core.webdriver.DriverConfigurationError;
import serenitycore.net.thucydides.core.webdriver.SupportedWebDriver;
import serenitycore.net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import serenitycore.net.thucydides.core.webdriver.WebDriverFactory;

public class WebDriverConfiguration<T extends DriverConfiguration> extends
    SystemPropertiesConfiguration implements DriverConfiguration  {

    /**
     * The default browser is Firefox.
     */
    public static final String DEFAULT_WEBDRIVER_DRIVER = "firefox";


    @Inject
    public WebDriverConfiguration(EnvironmentVariables environmentVariables) {
        super(environmentVariables);

    }

    /**
     * Get the currently-configured browser type.
     */
    public SupportedWebDriver getDriverType() {

        Optional<String> driverDefinedInEnvironment = Optional.ofNullable(WebDriverFactory.getDriverFrom(getEnvironmentVariables()));
        Optional<String> driverDefinedInTest = ThucydidesWebDriverSupport.getDefaultDriverType();

        String driverType = driverTypeOf(driverDefinedInTest.orElse(driverDefinedInEnvironment.orElse(DEFAULT_WEBDRIVER_DRIVER)));

        return lookupSupportedDriverTypeFor(driverType);
    }

    private String driverTypeOf(String driverName) {
        return (driverName.contains(":") ? driverName.substring(0, driverName.indexOf(":")) : driverName);
    }

    /**
     * Transform a driver type into the SupportedWebDriver enum. Driver type can
     * be any case.
     *
     * @throws DriverConfigurationError
     */
    private SupportedWebDriver lookupSupportedDriverTypeFor(final String driverType) {
        SupportedWebDriver driver = null;
        try {
            driver = SupportedWebDriver.getDriverTypeFor(driverType);
        } catch (IllegalArgumentException iae) {
            throwUnsupportedDriverExceptionFor(driverType);
        }
        return driver;
    }

    private void throwUnsupportedDriverExceptionFor(final String driverType) {
        throw new DriverConfigurationError(driverType
                + " is not a supported browser. Supported driver values are: "
                + SupportedWebDriver.listOfSupportedDrivers());
    }
}
