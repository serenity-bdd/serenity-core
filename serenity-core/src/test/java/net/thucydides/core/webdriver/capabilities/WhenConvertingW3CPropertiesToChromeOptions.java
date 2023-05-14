package net.thucydides.core.webdriver.capabilities;

import com.google.common.io.Resources;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.util.EnvironmentVariables;
import org.junit.Assume;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.chrome.ChromeDriverLogLevel;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LoggingPreferences;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

@DisplayName("When converting W3C properties to a ChromeOptions object")
class WhenConvertingW3CPropertiesToChromeOptions {
    private static EnvironmentVariables from(String testConfig) {
        Path configFilepath = new File(Resources.getResource(testConfig).getPath()).toPath();
        return SystemEnvironmentVariables.createEnvironmentVariables(configFilepath, new SystemEnvironmentVariables());
    }

    @DisplayName("If no webdriver section is present, use a standard ChromeOptions object")
    @Test
    void withNoWebdriverSection() {
        EnvironmentVariables environmentVariables = from("sample-conf-files/chrome/empty.conf");
        ChromeOptions options = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").chromeOptions();

        assertThat(options).isNotNull();
        assertThat(options.getBrowserName()).isEqualTo("chrome");
    }

    @DisplayName("If no webdriver.capabilities section is present, use a standard ChromeOptions object")
    @Test
    void withNoWebdriverCapabilitiesSection() {
        EnvironmentVariables environmentVariables = from("sample-conf-files/chrome/empty-capabilities.conf");
        ChromeOptions options = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").chromeOptions();

        assertThat(options).isNotNull();
        assertThat(options.getBrowserName()).isEqualTo("chrome");
    }


    @DisplayName("It should read the configuration from an environment-specific section if specified")
    @Test
    void withEnvironmentSpecificConfigurations() {
        EnvironmentVariables environmentVariables = from("sample-conf-files/chrome/with-environments.conf");
        ChromeOptions options = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").chromeOptions();

        assertThat(options).isNotNull();
        assertThat(options.getBrowserName()).isEqualTo("Chrome");
    }

    @Nested
    @DisplayName("We can specify browser preferences such as")
    class CanSpecifyDriverDetails {
        EnvironmentVariables environmentVariables = from("sample-conf-files/chrome/complete.conf");
        ChromeOptions options = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").chromeOptions();

        @Test
        void browserName() {
            assertThat(options.getBrowserName()).isEqualTo("Chrome");
        }

        @Test
        void browserVersion() {
            assertThat(options.getBrowserVersion()).isEqualTo("103.0");
        }

        @Test
        void platformName() {
            assertThat(options.getPlatformName()).isEqualTo(Platform.WIN11);
        }

        @Test
        void logLevel() {
            assertThat(options.getLogLevel()).isEqualTo(ChromeDriverLogLevel.INFO);
        }
    }

    @Nested
    @DisplayName("We can specify W3C cabailities such as")
    class CanSpecifyW3CCapabilities {
        EnvironmentVariables environmentVariables = from("sample-conf-files/chrome/complete.conf");
        ChromeOptions options = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").chromeOptions();

        @Test
        void screenResolution() {
            assertThat(options.getCapability("screenResolution")).isEqualTo("1280x1024");
        }

        @Test
        void insecureCertificates() {
            assertThat(options.getCapability("acceptInsecureCerts")).isEqualTo(true);
        }

        @Test
        void unhandledPromptBehaviour() {
            assertThat(options.getCapability("unhandledPromptBehavior")).isEqualTo(UnexpectedAlertBehaviour.ACCEPT);
        }

        @Test
        void strictFileInteractability() {
            assertThat(options.getCapability("strictFileInteractability")).isEqualTo(true);
        }

        @Test
        void proxy() {
            Proxy proxy = (Proxy) options.getCapability("proxy");
            assertThat(proxy.getHttpProxy()).isEqualTo("my.proxy.org");
            assertThat(proxy.getProxyType()).isEqualTo(Proxy.ProxyType.MANUAL);
        }

        @Test
        void timeouts() {
            Map<String, Object> timeouts = (Map<String, Object>) options.getCapability("timeouts");
            assertThat(timeouts.get("implicit")).isEqualTo(1000);
            assertThat(timeouts.get("pageLoad")).isEqualTo(1000);
            assertThat(timeouts.get("script")).isEqualTo(1000);
        }
    }

    @Nested
    @DisplayName("We can specify Android-specific options such as")
    class AndroidSpecificOptions {

        EnvironmentVariables environmentVariables = from("sample-conf-files/chrome/complete.conf");
        ChromeOptions options = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").chromeOptions();
        Map<String, Object> chromeCapacities = (Map<String, Object>) options.getCapability("goog:chromeOptions");

        @Test
        void androidPackage() {
            assertThat(chromeCapacities.get("androidPackage")).isEqualTo("PACKAGE");
        }

        @Test
        void androidActivity() {
            assertThat(chromeCapacities.get("androidActivity")).isEqualTo("ACTIVITY");
        }

        @Test
        void androidDeviceSerial() {
            assertThat(chromeCapacities.get("androidDeviceSerial")).isEqualTo("123");
        }

        @Test
        void androidProcess() {
            assertThat(chromeCapacities.get("androidProcess")).isEqualTo("PROCESS");
        }

        @Test
        void androidUseRunningApp() {
            assertThat(chromeCapacities.get("androidUseRunningApp")).isEqualTo(true);
        }
    }

    @Nested
    @DisplayName("We can configure chrome driver preferences such as")
    class ConfiguringChromePrefs {
        EnvironmentVariables environmentVariables = from("sample-conf-files/chrome/complete.conf");
        ChromeOptions options = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").chromeOptions();
        Map<String, Object> chromeOptions = (Map<String, Object>) options.getCapability("goog:chromeOptions");
        Map<String, Object> prefs = (Map<String, Object>) chromeOptions.get("prefs");

        @Test
        @DisplayName("the download directory")
        void downloadDir() {
            String downloadDirectory = prefs.get("download.default_directory").toString();
            assertThat(downloadDirectory).endsWith("/some/download/dir");
        }

        @Test
        @DisplayName("the prompt for download option")
        void downloadPrompt() {
            assertThat(prefs.get("download.prompt_for_download")).isEqualTo(true);
        }
    }


    /*
     * localState doesn't seem to be supported in the current WebDriver implementations.
     */
//    @Nested
//    @DisplayName("We can configure chrome driver local state")
//    class ConfiguringLocalState {
//        EnvironmentVariables environmentVariables = from("sample-conf-files/chrome/complete.conf");
//        ChromeOptions options = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").chromeOptions();
//        Map<String, Object> chromeOptions = (Map<String, Object>) options.getCapability("goog:chromeOptions");
//        Map<String, Object> localState = (Map<String, Object>) chromeOptions.get("localState");
//
//        @Test
//        @DisplayName("String values")
//        void stringValues() {
//            assertThat(localState.get("color")).isEqualTo("red");
//        }
//
//        @Test
//        @DisplayName("Numerical values")
//        void intValues() {
//            assertThat(localState.get("quantity")).isEqualTo(1);
//        }
//
//        @Test
//        @DisplayName("List values")
//        void listValues() {
//            assertThat((List<Integer>)localState.get("listOfValues")).containsExactly(1,2,3);
//        }
//
//        @Test
//        @DisplayName("Boolean values")
//        void boolValues() {
//            assertThat(localState.get("max")).isEqualTo(false);
//        }
//    }

    @Nested
    @DisplayName("We can configure chrome driver options such as")
    class ConfiguringChromeOptions {

        EnvironmentVariables environmentVariables = from("sample-conf-files/chrome/complete.conf");
        ChromeOptions options = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").chromeOptions();
        Map<String, Object> chromeOptions = (Map<String, Object>) options.getCapability("goog:chromeOptions");

        @Test
        @DisplayName("the path to the chrome binary")
        void chomeBinary() {
            String chromeBinary = chromeOptions.get("binary").toString();
            assertThat(chromeBinary).endsWith("/path/to/chromedriver");
        }

        @Test
        @DisplayName("chrome driver arguments")
        void chomeArgs() {
            List<String> args = (List<String>) chromeOptions.get("args");
            assertThat(args).contains("start-maximized")
                    .contains("window-size=1000,800")
                    .contains("headless");
        }
    }

    @Nested
    @DisplayName("We can configure logging preferences")
    class ConfiguringLoggingPreferences {
        EnvironmentVariables environmentVariables = from("sample-conf-files/chrome/complete.conf");
        ChromeOptions options = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").chromeOptions();

        @Test
        @DisplayName("including the overall logging level")
        void logLevel() {
            assertThat(options.getLogLevel()).isEqualTo(ChromeDriverLogLevel.INFO);
        }

        @Test
        @DisplayName("including the detailed logging preferences")
        void loggingPrefs() {
            LoggingPreferences loggingPreferences = (LoggingPreferences) options.getCapability("goog:loggingPrefs");
            assertThat(loggingPreferences.getLevel("browser")).isEqualTo(Level.ALL);
            assertThat(loggingPreferences.getLevel("driver")).isEqualTo(Level.ALL);
        }
    }

}
