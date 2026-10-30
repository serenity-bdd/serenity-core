package cfg;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.util.EnvironmentVariables;

/** A step library. Serenity injects the EnvironmentVariables field. */
public class ConfigReader {

    EnvironmentVariables environmentVariables;

    public String appUrl() {
        return EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("app.url").orElse("<none>");
    }

    public boolean injected() {
        return environmentVariables != null;
    }
}
