package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.webdriver.servicepools.DriverServicePool;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_USE_DRIVER_SERVICE_POOL;

public class ProvideNewDriver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProvideNewDriver.class);

    public static WebDriver withConfiguration(EnvironmentVariables environmentVariables,
                                        DesiredCapabilities capabilities,
                                        DriverServicePool pool,
                                        InstantiateDriver servicePoolStrategy,
                                        InstantiateDriver defaultStrategy) {

        if (useServicePoolIfConfigueredIn(environmentVariables)) {
            LOGGER.info("Instantiating driver using a service pool");
            LOGGER.info("Driver capabilities: {}", capabilities);
            try {
                pool.ensureServiceIsRunning();
                return servicePoolStrategy.newDriver(pool, capabilities);
            } catch (ServicePoolError | IOException couldNotStartService) {
                LOGGER.warn("Failed to start the driver service, using a native driver instead",
                        couldNotStartService.getMessage());

                return defaultStrategy.newDriver(pool, capabilities);
            }

        } else {
            LOGGER.info("Instantiating driver");
            LOGGER.info("Driver capabilities: {}", capabilities);
            return defaultStrategy.newDriver(pool, capabilities);
        }
    }

    private static boolean useServicePoolIfConfigueredIn(EnvironmentVariables environmentVariables) {
        return WEBDRIVER_USE_DRIVER_SERVICE_POOL.booleanFrom(environmentVariables, false);
    }
}
