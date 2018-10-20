package net.serenitybdd.core.webdriver.driverproviders;

import org.openqa.selenium.Capabilities;

import java.util.Properties;

public class CapabilitiesToPropertiesConverter {
    public static Properties capabilitiesToProperties(Capabilities capabilities) {
        Properties properties = new Properties();

        if (capabilities.getPlatform() != null) {
            properties.setProperty("platform", capabilities.getPlatform().name());
        }

        for (String capability : capabilities.asMap().keySet()) {
            if (capabilities.getCapability(capability) instanceof String) {
                properties.setProperty(capability, capabilities.getCapability(capability).toString());
            }
        }
        return properties;
    }
}
