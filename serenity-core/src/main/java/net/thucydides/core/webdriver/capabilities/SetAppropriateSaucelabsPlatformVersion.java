package net.thucydides.core.webdriver.capabilities;

import com.google.common.collect.Maps;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;

class SetAppropriateSaucelabsPlatformVersion {
    private final DesiredCapabilities capabilities;

    private static Map<String, String> OS_PLATFORM_NAMES = Maps.newHashMap();
    static {
        OS_PLATFORM_NAMES.put("snowleopard", "OS X 10.6");
        OS_PLATFORM_NAMES.put("snow leopard", "OS X 10.6");
        OS_PLATFORM_NAMES.put("mountainlion", "OS X 10.8");
        OS_PLATFORM_NAMES.put("mountain lion", "OS X 10.8");
        OS_PLATFORM_NAMES.put("mavericks", "OS X 10.9");
        OS_PLATFORM_NAMES.put("yosemite", "OS X 10.10");
        OS_PLATFORM_NAMES.put("elcapitan", "OS X 10.11");
        OS_PLATFORM_NAMES.put("el capitan", "OS X 10.11");
    }

    private static Map<String, String> MAC_OS_VERSIONS_PER_SAFARI_VERSION = Maps.newHashMap();
    static {
        MAC_OS_VERSIONS_PER_SAFARI_VERSION.put("5", "OS X 10.6");
        MAC_OS_VERSIONS_PER_SAFARI_VERSION.put("6", "OS X 10.8");
        MAC_OS_VERSIONS_PER_SAFARI_VERSION.put("7", "OS X 10.9");
        MAC_OS_VERSIONS_PER_SAFARI_VERSION.put("8", "OS X 10.10");
        MAC_OS_VERSIONS_PER_SAFARI_VERSION.put("10", "OS X 10.11");
    }

    public SetAppropriateSaucelabsPlatformVersion(DesiredCapabilities capabilities) {

        this.capabilities = capabilities;
    }

    public static SetAppropriateSaucelabsPlatformVersion inCapabilities(DesiredCapabilities capabilities) {
        return new SetAppropriateSaucelabsPlatformVersion(capabilities);
    }

    public void from(EnvironmentVariables environmentVariables) {
        String platformValue = ThucydidesSystemProperty.SAUCELABS_TARGET_PLATFORM
                                                       .from(environmentVariables)
                                                       .toLowerCase();

        if (isEmpty(platformValue)) {
            return;
        }
        if (OS_PLATFORM_NAMES.containsKey(platformValue)) {
            capabilities.setCapability("platform",OS_PLATFORM_NAMES.get(platformValue));
        } else {
            capabilities.setCapability("platform", platformFrom(platformValue));
        }

        if (capabilities.getBrowserName().equals("safari")) {
            setAppropriateSaucelabsPlatformVersionForSafariFrom(environmentVariables);
        }

    }

    private void setAppropriateSaucelabsPlatformVersionForSafariFrom(EnvironmentVariables environmentVariables)
    {
        if (ThucydidesSystemProperty.SAUCELABS_TARGET_PLATFORM.from(environmentVariables).equalsIgnoreCase("mac"))
        {
            String browserVersion = ThucydidesSystemProperty.SAUCELABS_DRIVER_VERSION.from(environmentVariables);
            if (MAC_OS_VERSIONS_PER_SAFARI_VERSION.containsKey(browserVersion)) {
                capabilities.setCapability("platform", MAC_OS_VERSIONS_PER_SAFARI_VERSION.get(browserVersion));
            }
        }
    }

    private Platform platformFrom(String platformValue) {
        return Platform.valueOf(platformValue.toUpperCase());
    }

}
