package net.serenitybdd.core.webdriver.driverproviders;

import com.google.gson.JsonObject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Proxy;
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

    public void in(DesiredCapabilities capabilities) {
        ConfiguredProxy.definedIn(environmentVariables).ifPresent(
                proxy -> capabilities.setCapability(CapabilityType.PROXY, proxy)
        );
    }
}
