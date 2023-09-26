package net.thucydides.core.webdriver.capabilities;

import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Build a ChromeOptions or EdgeOptions object from a set of Desired Capabilities defined in the serenity.conf file.
 */
public class FirefoxOptionsBuilder {

    public static FirefoxOptions fromDesiredCapabilities(DesiredCapabilities capabilities) {
        FirefoxOptions firefoxOptions = new FirefoxOptions(capabilities);
        SetBrowserVersionAndPlatform.from(capabilities).in(firefoxOptions);
        SetCommonBrowserOptions.from(capabilities.asMap()).in(firefoxOptions);

        // Firefox-specific capabilities
        if (capabilities.getCapability(FirefoxOptions.FIREFOX_OPTIONS) != null) {
            Map<String, Object> options = NestedMap.called(FirefoxOptions.FIREFOX_OPTIONS).from(capabilities.asMap());

            List<String> firefoxSpecificOptions =
                    options.keySet().stream()
                            .filter(field -> !SetCommonBrowserOptions.propertyNames().contains(field))
                            .collect(Collectors.toList());

            for (String optionName : firefoxSpecificOptions) {
                switch (optionName) {
                    // Absolute path to the custom Firefox binary to use
                    case "binary":
                        FirefoxBinary binary = new FirefoxBinary(new File(options.get("binary").toString()));
                        firefoxOptions.setBinary(binary);
                        break;

                    // Command line arguments to pass to the Firefox binary
                    case "args":
                        if (options.get("args") instanceof List) {
                            List<String> args = ListOfValues.from(options).forProperty("args");
                            firefoxOptions.getBinary().addCommandLineOptions(args.toArray(new String[]{}));
                        }
                        break;

                    // Overall log level
                    case "log":
                        Map<String, Object> logLevels = NestedMap.called("log").from(options);
                        if (logLevels != null && logLevels.containsKey("level")) {
                            firefoxOptions.setLogLevel(FirefoxDriverLogLevel.fromString(logLevels.get("level").toString()));
                        }
                        break;
                    case "androidPackage":
                        firefoxOptions.setAndroidPackage(options.get("androidPackage").toString());
                        break;
                    case "androidActivity":
                        firefoxOptions.setAndroidActivity(options.get("androidActivity").toString());
                        break;
                    case "androidDeviceSerial":
                        firefoxOptions.setAndroidDeviceSerialNumber(options.get("androidDeviceSerial").toString());
                        break;
                    case "androidIntentArguments":
                        firefoxOptions.setAndroidIntentArguments(ListOfValues.from(options).forProperty("androidIntentArguments"));
                        break;
                }
            }
        }
        return firefoxOptions;
    }
}
