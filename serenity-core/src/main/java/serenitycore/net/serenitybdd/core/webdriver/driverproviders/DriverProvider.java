package serenitycore.net.serenitybdd.core.webdriver.driverproviders;

import serenitymodel.net.thucydides.core.ThucydidesSystemProperty;
import serenitymodel.net.thucydides.core.guice.Injectors;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.util.Properties;

public interface DriverProvider {

    default WebDriver newInstance(String options) throws MalformedURLException {
        return newInstance(options, Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }
    WebDriver newInstance(String options, EnvironmentVariables environmentVariables) throws MalformedURLException ;

    default Properties capabilitiesToProperties(Capabilities capabilities) {
        return CapabilitiesToPropertiesConverter.capabilitiesToProperties(capabilities);
    }

    default boolean isDriverAutomaticallyDownloaded(EnvironmentVariables environmentVariables) {
        return ThucydidesSystemProperty.WEBDRIVER_AUTODOWNLOAD.booleanFrom(environmentVariables, true);
    }
}
