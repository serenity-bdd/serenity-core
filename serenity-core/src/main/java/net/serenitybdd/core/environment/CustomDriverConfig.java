package net.serenitybdd.core.environment;

import com.typesafe.config.Config;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.MutableCapabilities;

import java.util.Optional;

public class CustomDriverConfig {

    public static String fetchContextFrom(MutableCapabilities capabilities, EnvironmentVariables environmentVariables, String name) {
        String browserName = capabilities.getBrowserName();

        Optional<Config> ltOptions = webdriverCapabilitiesConfig(environmentVariables, name);

        String os = null;
        if (ltOptions.isPresent() && ltOptions.get().hasPath("platformName")) {
            os = ltOptions.get().getString("platformName");
        }
        return (os != null) ? browserName + ", " + os : browserName;
    }


    public static Optional<Config> webdriverCapabilitiesConfig(EnvironmentVariables environmentVariables, String name) {
        Config webdriverCapabilities = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getConfig("webdriver.capabilities");

        if (webdriverCapabilities.hasPath(name)) {
            return Optional.of(webdriverCapabilities.getConfig(name));
        } else {
            return Optional.empty();
        }
    }
}
