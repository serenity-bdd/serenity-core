package net.thucydides.core.webdriver.capabilities;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;

class SetAppropriateSaucelabsPlatformVersion {
    private final DesiredCapabilities capabilities;

    private static String DEFAULT_PLATFORM = "Windows 10";

    private static Map<String, String> OS_PLATFORM_NAMES = new HashMap();

    static {
        OS_PLATFORM_NAMES.put("snowleopard", "OS X 10.6");
        OS_PLATFORM_NAMES.put("snow leopard", "OS X 10.6");
        OS_PLATFORM_NAMES.put("mountainlion", "OS X 10.8");
        OS_PLATFORM_NAMES.put("mountain lion", "OS X 10.8");
        OS_PLATFORM_NAMES.put("mavericks", "OS X 10.9");
        OS_PLATFORM_NAMES.put("yosemite", "OS X 10.10");
        OS_PLATFORM_NAMES.put("elcapitan", "OS X 10.11");
        OS_PLATFORM_NAMES.put("el capitan", "OS X 10.11");
        OS_PLATFORM_NAMES.put("sierra", "macOS 10.12");
        OS_PLATFORM_NAMES.put("high sierra", "macOS 10.13");
    }

    private static Map<String, String> MAC_OS_VERSIONS_PER_SAFARI_VERSION = new HashMap();

    static {
        MAC_OS_VERSIONS_PER_SAFARI_VERSION.put("5", "OS X 10.6");
        MAC_OS_VERSIONS_PER_SAFARI_VERSION.put("6", "OS X 10.8");
        MAC_OS_VERSIONS_PER_SAFARI_VERSION.put("7", "OS X 10.9");
        MAC_OS_VERSIONS_PER_SAFARI_VERSION.put("8", "OS X 10.10");
        MAC_OS_VERSIONS_PER_SAFARI_VERSION.put("10", "OS X 10.11");
        MAC_OS_VERSIONS_PER_SAFARI_VERSION.put("11", "macOS 10.13");
    }

    public SetAppropriateSaucelabsPlatformVersion(DesiredCapabilities capabilities) {

        this.capabilities = capabilities;
    }

    public static SetAppropriateSaucelabsPlatformVersion inCapabilities(DesiredCapabilities capabilities) {
        return new SetAppropriateSaucelabsPlatformVersion(capabilities);
    }

    public void from(EnvironmentVariables environmentVariables) {
        String platformValue = ThucydidesSystemProperty.SAUCELABS_TARGET_PLATFORM
                .from(environmentVariables, DEFAULT_PLATFORM)
                .toLowerCase();

        if (isEmpty(platformValue)) {
            return;
        }
        if (OS_PLATFORM_NAMES.containsKey(platformValue)) {
            capabilities.setCapability("platform", OS_PLATFORM_NAMES.get(platformValue));
        } else {
            capabilities.setCapability("platform", platformFrom(platformValue));
        }

        if (capabilities.getBrowserName().equals("safari")) {
            setAppropriateSaucelabsPlatformVersionForSafariFrom(environmentVariables);
        }

    }

    private void setAppropriateSaucelabsPlatformVersionForSafariFrom(EnvironmentVariables environmentVariables) {
        if (ThucydidesSystemProperty.SAUCELABS_TARGET_PLATFORM.from(environmentVariables).equalsIgnoreCase("mac")) {
            String browserVersion = ThucydidesSystemProperty.SAUCELABS_BROWSER_VERSION.from(environmentVariables);
            if (MAC_OS_VERSIONS_PER_SAFARI_VERSION.containsKey(browserVersion)) {
                capabilities.setCapability("platform", MAC_OS_VERSIONS_PER_SAFARI_VERSION.get(browserVersion));
            }
        }
    }

    private Platform platformFrom(String platformValue) {
        return Arrays.stream(Platform.values()).filter(
                platform -> platform.name().equalsIgnoreCase(platformValue) ||
                            platformNameIn(platformValue, platform.getPartOfOsName())
        ).findFirst()
         .orElseThrow(() -> new UnknownPlatformException(platformValue));
    }

    private boolean platformNameIn(String platformValue, String[] partOfOsName) {
        return Arrays.stream(partOfOsName)
                     .anyMatch( osName -> osName.equalsIgnoreCase(platformValue));
    }

    private static class UnknownPlatformException extends RuntimeException {
        public UnknownPlatformException(String message) {
            super(message);
        }
    }
}
