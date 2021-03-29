package serenitycore.net.serenitybdd.core.webdriver.driverproviders;

import serenitymodel.net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.remote.DesiredCapabilities;

public class ProvidedDriverCapabilities implements DriverCapabilitiesProvider {

    private final EnvironmentVariables environmentVariables;

    public ProvidedDriverCapabilities(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    @Override
    public DesiredCapabilities getCapabilities() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setJavascriptEnabled(true);
        return capabilities;
    }

}
