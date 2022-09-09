package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.CurrentOS;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.Optional;

public class UpdateDriverEnvironmentProperty {
    public static void forDriverProperty(String driverProperty) {
        //
        // If the driver binary is not available on the system path, attempt to update it using the environment properties
        //
        if (!DriverExecutableIsDefined.bySystemProperty(driverProperty)) {
            EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);

            Optional<String> defaultConfiguredDriver = environmentVariables.optionalProperty(driverProperty);

            String osSpecificDriverProperty = osSpecific(driverProperty);
            Optional<String> optionalDriverPath
                    = EnvironmentSpecificConfiguration
                    .from(environmentVariables)
                    .getOptionalProperty(osSpecificDriverProperty);

            String resolvedDriverProperty = defaultConfiguredDriver.orElse(optionalDriverPath.orElse(null));

            if (resolvedDriverProperty != null) {
                System.setProperty(driverProperty, resolvedDriverProperty);
            }
        }
    }

    private static String osSpecific(String exeProperty) {
        return "drivers." + CurrentOS.getType() + "." + exeProperty;
    }
}
