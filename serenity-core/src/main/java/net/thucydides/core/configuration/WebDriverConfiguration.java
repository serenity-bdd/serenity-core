package net.thucydides.core.configuration;

import com.google.inject.*;
import net.thucydides.core.util.*;
import net.thucydides.core.webdriver.*;

import java.util.Optional;

public class WebDriverConfiguration<T extends DriverConfiguration> extends SystemPropertiesConfiguration implements DriverConfiguration  {

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

        String driverType =  driverDefinedInTest.orElse(driverDefinedInEnvironment.orElse(DEFAULT_WEBDRIVER_DRIVER));

        return lookupSupportedDriverTypeFor(driverType);
    }

    /**
     * Transform a driver type into the SupportedWebDriver enum. Driver type can
     * be any case.
     *
     * @throws UnsupportedDriverException
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
        throw new UnsupportedDriverException(driverType
                + " is not a supported browser. Supported driver values are: "
                + SupportedWebDriver.listOfSupportedDrivers());
    }

    @Override
    public WebDriverConfiguration copy() {
        return withEnvironmentVariables(getEnvironmentVariables());
    }


    @Override
    public WebDriverConfiguration withEnvironmentVariables(EnvironmentVariables environmentVariables) {
        WebDriverConfiguration copy = new WebDriverConfiguration(environmentVariables.copy());
        copy.outputDirectory = null; // Reset to be reloaded from the System properties
        copy.defaultBaseUrl = defaultBaseUrl;
        return copy;
    }

}
