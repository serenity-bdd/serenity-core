package net.thucydides.core.webdriver.capabilities;

import com.google.common.io.Resources;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assume.assumeThat;

@DisplayName("When converting W3C properties to a ChromeOptions object")
class WhenConvertingW3CPropertiesToFirefoxOptions {
    private static EnvironmentVariables from(String testConfig) {
        Path configFilepath = new File(Resources.getResource(testConfig).getPath()).toPath();
        return SystemEnvironmentVariables.createEnvironmentVariables(configFilepath, new SystemEnvironmentVariables());
    }

    @BeforeEach
    void linuxOnlyTests() {
        String osName = System.getProperty("os.name");
        assumeThat(osName, not(containsString("windows")));
    }

    @DisplayName("If no webdriver section is present, use a standard FirefoxOptions object")
    @Test
    void withNoWebdriverSection() {
        EnvironmentVariables environmentVariables = from("sample-conf-files/firefox/empty.conf");
        FirefoxOptions options = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").firefoxOptions();

        assertThat(options).isNotNull();
        assertThat(options.getBrowserName()).isEqualTo("firefox");
    }

    @DisplayName("If no webdriver.capabilities section is present, use a standard FirefoxOptions object")
    @Test
    void withNoWebdriverCapabilitiesSection() {
        EnvironmentVariables environmentVariables = from("sample-conf-files/chrome/empty-capabilities.conf");
        FirefoxOptions options = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").firefoxOptions();

        assertThat(options).isNotNull();
        assertThat(options.getBrowserName()).isEqualTo("firefox");
    }


    @Nested
    @DisplayName("We can specify browser preferences such as")
    class CanSpecifyDriverDetails {
        EnvironmentVariables environmentVariables = from("sample-conf-files/firefox/complete.conf");
        FirefoxOptions options = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").firefoxOptions();

        @Test
        void browserName() {
            assertThat(options.getBrowserName()).isEqualTo("firefox");
        }

        @Test
        void browserVersion() {
            assertThat(options.getBrowserVersion()).isEqualTo("101.0.1");
        }

        @Test
        void platformName() {
            assertThat(options.getPlatformName()).isEqualTo(Platform.WIN11);
        }
    }

    @Nested
    @DisplayName("We can specify W3C cabailities such as")
    class CanSpecifyW3CCapabilities {
        EnvironmentVariables environmentVariables = from("sample-conf-files/firefox/complete.conf");
        FirefoxOptions options = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").firefoxOptions();

        @Test
        void insecureCertificates() {
            assertThat(options.getCapability("acceptInsecureCerts")).isEqualTo(true);
        }

        @Test
        void unhandledPromptBehaviour() {
            assertThat(options.getCapability("unhandledPromptBehavior")).isEqualTo(UnexpectedAlertBehaviour.DISMISS);
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
    @DisplayName("We can configure driver options such as")
    class ConfiguringFirefoxOptions {

        EnvironmentVariables environmentVariables = from("sample-conf-files/firefox/complete.conf");
        FirefoxOptions options = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").firefoxOptions();
        Map<String, Object> firefoxOptions = (Map<String, Object>) options.getCapability("moz:firefoxOptions");
    }

    @Nested
    @DisplayName("We can configure firefox driver preferences such as")
    class ConfiguringChromePrefs {
        EnvironmentVariables environmentVariables = from("sample-conf-files/firefox/complete.conf");
        FirefoxOptions options = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").firefoxOptions();
        Map<String, Object> firefoxOptions = (Map<String, Object>) options.getCapability("moz:firefoxOptions");
        Map<String, Object> prefs = (Map<String, Object>) firefoxOptions.get("prefs");

        @Test
        @DisplayName("dom.ipc.processCount")
        void processCount() {
            int processCount = (Integer) prefs.get("dom.ipc.processCount");
            assertThat(processCount).isEqualTo(8);
        }

        @Test
        @DisplayName("javascript.options.showInConsole")
        void downloadPrompt() {
            assertThat(prefs.get("javascript.options.showInConsole")).isEqualTo(false);
        }
    }

    @Nested
    @DisplayName("We can configure logging preferences")
    class ConfiguringLoggingPreferences {
        EnvironmentVariables environmentVariables = from("sample-conf-files/firefox/complete.conf");
        FirefoxOptions options = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").firefoxOptions();
        Map<String, Object> firefoxOptions = (Map<String, Object>) options.getCapability("moz:firefoxOptions");

        @Test
        @DisplayName("including the overall logging level")
        void logLevel() {
            assertThat(((Map)firefoxOptions.get("log")).get("level")).isEqualTo("trace");
        }
    }
}
