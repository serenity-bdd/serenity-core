package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.Optional;

public class InsecureCertConfig {

    public static Optional<Boolean> acceptInsecureCertsDefinedIn(EnvironmentVariables environmentVariables) {
        return EnvironmentSpecificConfiguration
                .from(environmentVariables)
                .getOptionalProperty("webdriver.capabilities.acceptInsecureCerts","accept.insecure.certs")
                .map(Boolean::parseBoolean);
    }
}
