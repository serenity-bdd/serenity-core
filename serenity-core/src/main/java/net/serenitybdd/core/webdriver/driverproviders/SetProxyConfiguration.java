package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class SetProxyConfiguration {
    private final EnvironmentVariables environmentVariables;

    public SetProxyConfiguration(EnvironmentVariables environmentVariables) {

        this.environmentVariables = environmentVariables;
    }

    public static SetProxyConfiguration from(EnvironmentVariables environmentVariables) {
        return new SetProxyConfiguration(environmentVariables);
    }

    public void in(MutableCapabilities capabilities) {
        ConfiguredProxy.definedIn(environmentVariables).ifPresent(
                proxy -> capabilities.setCapability(CapabilityType.PROXY, proxy)
        );
    }
}
