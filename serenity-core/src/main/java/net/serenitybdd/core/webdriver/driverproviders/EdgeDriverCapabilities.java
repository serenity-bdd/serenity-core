package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.BrowserPreferences;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

public class EdgeDriverCapabilities implements DriverCapabilitiesProvider {

    private final EnvironmentVariables environmentVariables;

    public EdgeDriverCapabilities(EnvironmentVariables environmentVariables){
        this.environmentVariables = environmentVariables;
    }

    public DesiredCapabilities getCapabilities() {
        EdgeOptions edgeOptions = configuredOptions();
        String envSpecifiedEdgeOption = ThucydidesSystemProperty.EDGE_OPTIONS.from(environmentVariables, "");
        Map<String, Object> environmentSpecifiedOptions = CapabilitiesConverter.optionsToMap(envSpecifiedEdgeOption);
        addPreferencesTo(environmentSpecifiedOptions);

        edgeOptions.setCapability("ms:edgeOptions", environmentSpecifiedOptions);

        return new DesiredCapabilities(edgeOptions);
    }

    public EdgeOptions configuredOptions() {
        EdgeOptions options = new EdgeOptions();
        addProxyConfigurationTo(options);
        return options;
    }

    private void addPreferencesTo(Map<String, Object> options) {
        preferencesConfiguredIn(environmentVariables).forEach(options::put);
    }

    public static Map<String, Object> preferencesConfiguredIn(EnvironmentVariables environmentVariables) {
        return SanitisedBrowserPreferences.cleanUpPathsIn(
                BrowserPreferences.startingWith("edge.preferences.").from(environmentVariables)
        );
    }

    private void addProxyConfigurationTo(EdgeOptions options) {
        ConfiguredProxy.definedIn(environmentVariables).ifPresent(options::setProxy);
    }
}
