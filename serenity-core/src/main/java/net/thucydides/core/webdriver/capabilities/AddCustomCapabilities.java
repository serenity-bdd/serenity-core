package net.thucydides.core.webdriver.capabilities;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.MutableCapabilities;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class AddCustomCapabilities {
    private final String prefix;
    private boolean withPrefix = false;
    private EnvironmentVariables environmentVariables;

    public AddCustomCapabilities(String prefix) {
        this.prefix = prefix;
        this.environmentVariables = SystemEnvironmentVariables.currentEnvironmentVariables();
    }

    public static AddCustomCapabilities startingWith(String prefix) {
        return new AddCustomCapabilities(prefix);
    }

    public AddCustomCapabilities from(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        return this;
    }

    public void to(MutableCapabilities capabilities) {
        List<String> propertiesWithPrefix = environmentVariables.getKeys()
                .stream()
                .filter(key -> key.startsWith(prefix))
                .collect(Collectors.toList());

        for(String propertyKey : propertiesWithPrefix) {
            String preparedPropertyKey = getPreparedPropertyKey(propertyKey);

            String propertyValue = EnvironmentSpecificConfiguration.from(environmentVariables)
                    .getOptionalProperty(propertyKey)
                    .orElse(null);

            if (isNotEmpty(propertyValue)) {
                capabilities.setCapability(preparedPropertyKey, CapabilityProperty.asObject(propertyValue));
                if (withPrefix) {
                    capabilities.setCapability(propertyKey, CapabilityProperty.asObject(propertyValue));
                }
            }
        }
    }

    private String getPreparedPropertyKey(String propertyKey) {
        String shortenedPropertyKey = propertyKey.replace(prefix,"");
        if (shortenedPropertyKey.equals("os.version")) {
            return "os_version";
        } else if (shortenedPropertyKey.equals("browser.version")) {
            return "browser_version";
        }
        return shortenedPropertyKey;
    }

    public AddCustomCapabilities withAndWithoutPrefixes() {
        withPrefix = true;
        return this;
    }
}
