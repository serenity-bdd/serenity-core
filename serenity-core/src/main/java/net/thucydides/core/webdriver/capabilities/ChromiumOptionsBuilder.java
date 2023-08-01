package net.thucydides.core.webdriver.capabilities;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Build a ChromeOptions or EdgeOptions object from a set of Desired Capabilities defined in the serenity.conf file.
 */
public class ChromiumOptionsBuilder {

    public static ChromiumOptions<?> fromDesiredCapabilities(DesiredCapabilities capabilities,
                                                             ChromiumOptions<?> chromiumOptions,
                                                             String capabilitySectionName) {
        // General capabilities
        chromiumOptions.merge(chromiumOptions);
        chromiumOptions = (ChromiumOptions<?>) chromiumOptions.merge(capabilities);

        // Browser version and platform name
        SetBrowserVersionAndPlatform.from(capabilities).in(chromiumOptions);

        // Chromium-specific capabilities
        if (capabilities.getCapability(capabilitySectionName) != null) {
            Map<String, Object> options = NestedMap.called(capabilitySectionName).from(capabilities.asMap());

            SetCommonBrowserOptions.from(options).in(chromiumOptions);

            Map<String, Object> extraOptions = new HashMap<>();
            List<String> chromeSpecificOptions =
                    options.keySet().stream()
                            .filter(field -> !SetCommonBrowserOptions.propertyNames().contains(field))
                            .collect(Collectors.toList());

            for (String optionName : chromeSpecificOptions) {
                switch (optionName) {
                    // Arguments
                    case "args":
                        List<String> args = ListOfValues.from(options).forProperty("args");
                        if (!args.isEmpty()) {
                            chromiumOptions.addArguments(args);
                        }
                        if (args.contains("headless") || args.contains("--headless")) {
                            chromiumOptions.setHeadless(true);
                        }
                        break;
                    // Extensions
                    case "extensions":
                        if (!(options.get("extensions") instanceof List)) {
                            throw new InvalidCapabilityException("Invalid W3C capability: extensions should be a map but was " + options.get("extensions"));
                        }
                        if (!((List<?>) options.get("extensions")).isEmpty()) {
                            addExtensions((List<?>) options.get("extensions"), chromiumOptions);
                        }
                        break;
                    // Binary
                    case "binary":
                        chromiumOptions.setBinary(options.get("binary").toString());
                        break;
                    case "androidUseRunningApp":
                        chromiumOptions.setUseRunningAndroidApp(Boolean.parseBoolean(options.get("androidUseRunningApp").toString()));
                        break;
                    case "androidActivity":
                        chromiumOptions.setAndroidActivity(options.get("androidActivity").toString());
                        break;
                    case "androidDeviceSerial":
                        chromiumOptions.setAndroidDeviceSerialNumber(options.get("androidDeviceSerial").toString());
                        break;
                    case "androidPackage":
                        chromiumOptions.setAndroidPackage(options.get("androidPackage").toString());
                        break;
                    case "androidProcess":
                        chromiumOptions.setAndroidProcess(options.get("androidProcess").toString());
                        break;
                    case "prefs":
                        chromiumOptions.setExperimentalOption("prefs", NestedMap.called("prefs").from(options));
                        break;
                    case "excludeSwitches":
                        chromiumOptions.setExperimentalOption("excludeSwitches", ListOfValues.from(options).forProperty("excludeSwitches"));
                        break;
                    default:
                        extraOptions.put(optionName, options.get(optionName));
                }
            }
            Map<String, Object> browserSpecificOptions = NestedMap.called(capabilitySectionName).from(chromiumOptions.asMap());
            extraOptions.putAll(browserSpecificOptions);
            Map<String, Object> distinctExtraOptions = extraOptions.entrySet().stream().distinct().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            chromiumOptions.setCapability(capabilitySectionName, distinctExtraOptions);
        }

        // Log levels
        if (capabilities.getCapability(ChromeOptions.LOGGING_PREFS) != null) {
            setChromiumLoggingPreferences(capabilities, chromiumOptions);
        } else if (capabilities.getCapability(EdgeOptions.LOGGING_PREFS) != null) {
            setChromiumLoggingPreferences(capabilities, chromiumOptions);
        }

        return chromiumOptions;
    }

    private static void addExtensions(List<?> extensions, ChromiumOptions<?> options) {
        List<File> extensionFiles = extensions.stream().map(Object::toString).map(File::new).collect(Collectors.toList());
        options.addExtensions(extensionFiles);
    }

    private static void setChromiumLoggingPreferences(DesiredCapabilities capabilities, ChromiumOptions<?> options) {
        if (options instanceof ChromeOptions) {
            setLoggingPreferences(capabilities, ChromeOptions.LOGGING_PREFS, options);
        } else if (options instanceof EdgeOptions) {
            setLoggingPreferences(capabilities, EdgeOptions.LOGGING_PREFS, options);
        }
    }

    private static void setLoggingPreferences(DesiredCapabilities capabilities,
                                              String capabilityName,
                                              ChromiumOptions<?> options) {
        LoggingPreferences logPrefs = new LoggingPreferences();
        Object loggingPreferenceValue = capabilities.getCapability(capabilityName);
        if (!(loggingPreferenceValue instanceof Map)) {
            throw new InvalidCapabilityException("Invalid W3C capability: " + capabilityName + " should be a map but was " + loggingPreferenceValue);
        }

        Map<String, Object> loggingPreferenceValues = NestedMap.called(capabilityName).from(capabilities.asMap());
        loggingPreferenceValues.forEach(
                (key, value) -> logPrefs.enable(key, Level.parse(value.toString()))
        );
        options.setCapability(capabilityName, logPrefs);
    }
}
