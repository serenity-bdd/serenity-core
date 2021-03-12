package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

public class EdgeDriverCapabilities implements DriverCapabilitiesProvider {

    private final EnvironmentVariables environmentVariables;

    public EdgeDriverCapabilities(EnvironmentVariables environmentVariables){
        this.environmentVariables = environmentVariables;
    }

    public DesiredCapabilities getCapabilities() {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities(new EdgeOptions());

        String edgeOptions = ThucydidesSystemProperty.EDGE_OPTIONS.from(environmentVariables, "");
        desiredCapabilities.setCapability("ms:edgeOptions", CapabilitiesConverter.optionsToMap(edgeOptions));

        return desiredCapabilities;
    }
}
