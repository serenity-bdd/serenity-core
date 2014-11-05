package net.thucydides.core.webdriver;

import com.google.common.base.Preconditions;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;

import static net.thucydides.core.webdriver.WebDriverFactory.getDriverFrom;

/**
 * A description goes here.
 * User: john
 * Date: 7/03/2014
 * Time: 12:39 PM
 */
public class ProvidedDriverConfiguration {

    private final EnvironmentVariables environmentVariables;

    public ProvidedDriverConfiguration(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public boolean isProvided() {
        return getDriverFrom(environmentVariables).equals("provided");
    }

    public DriverSource getDriverSource() {
        String providedDriverType = getDriverName();
        Preconditions.checkNotNull(providedDriverType, "No provider type was specified in 'webdriver.provided.type'");

        String providedImplementation = environmentVariables.getProperty("webdriver.provided." + providedDriverType);
        Preconditions.checkNotNull(providedImplementation,
                "No provider implementation was specified in 'webdriver.provided.'" + providedDriverType);

        try {
            return (DriverSource) Class.forName(providedImplementation).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate the custom webdriver provider of type " + providedImplementation);
        }
    }

    public String getDriverName() {
        return environmentVariables.getProperty(ThucydidesSystemProperty.WEBDRIVER_PROVIDED_TYPE);
    }
}
