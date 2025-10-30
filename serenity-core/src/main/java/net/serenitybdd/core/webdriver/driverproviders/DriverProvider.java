package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.util.Properties;

public interface DriverProvider {

    default WebDriver newInstance(String options) throws MalformedURLException {
        return newInstance(options, SystemEnvironmentVariables.currentEnvironmentVariables());
    }
    WebDriver newInstance(String options, EnvironmentVariables environmentVariables);

    default Properties capabilitiesToProperties(Capabilities capabilities) {
        return CapabilitiesConverter.capabilitiesToProperties(capabilities);
    }

    default boolean isDriverAutomaticallyDownloaded(EnvironmentVariables environmentVariables) {
        return ThucydidesSystemProperty.WEBDRIVER_AUTODOWNLOAD.booleanFrom(environmentVariables, true);
    }
}
