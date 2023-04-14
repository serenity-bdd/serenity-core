package net.thucydides.core.webdriver.capabilities;

import com.google.common.io.Resources;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.util.EnvironmentVariables;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assume.assumeThat;

public class WhenSettingW3CProperties {

    private static EnvironmentVariables from(String testConfig) {
        Path configFilepath = new File(Resources.getResource(testConfig).getPath()).toPath();
        return SystemEnvironmentVariables.createEnvironmentVariables(configFilepath, new SystemEnvironmentVariables());
    }

    @Test
    public void shouldReadW3COptionsFromConfFile() {
        EnvironmentVariables environmentVariables = from("sample-conf-files/simple.conf");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();

        assertThat(caps.getBrowserName()).isEqualTo("Chrome");
        assertThat(caps.getBrowserVersion()).isEqualTo("103.0");
        assertThat(caps.getPlatformName()).isEqualTo(Platform.WIN11);
    }

    @Test
    public void shouldReadFromEnvironmentSpecificConfigs() {
        EnvironmentVariables environmentVariables = from("sample-conf-files/env-specific-simple.conf");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();

        assertThat(caps.getBrowserName()).isEqualTo("Chrome");
        assertThat(caps.getBrowserVersion()).isEqualTo("103.0");
        assertThat(caps.getPlatformName()).isEqualTo(Platform.WIN11);
    }

    @Test
    public void shouldReadW3CStringValues() {
        EnvironmentVariables environmentVariables = from("sample-conf-files/with-typed-values.conf");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();

        assertThat(caps.getCapability("unhandledPromptBehavior")).isEqualTo("dismiss");
    }

    @Test
    public void shouldReadW3CBooleanValues() {
        EnvironmentVariables environmentVariables = from("sample-conf-files/with-typed-values.conf");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();

        assertThat(caps.getCapability("acceptInsecureCerts")).isEqualTo(true);
    }

    @Test
    public void shouldReadW3CNumericalValues() {
        EnvironmentVariables environmentVariables = from("sample-conf-files/with-typed-values.conf");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();

        assertThat(caps.getCapability("maxDuration")).isEqualTo(10000);
    }

    @Test
    public void shouldSupportW3CListOfStrings() {
        EnvironmentVariables environmentVariables = from("sample-conf-files/with-typed-values.conf");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();

        assertThat(caps.getCapability("tags")).isInstanceOf(List.class);
        assertThat((List<String>) caps.getCapability("tags")).containsExactly("red","green","blue");
    }

    @Test
    public void shouldSupportW3CListOfNumbers() {
        EnvironmentVariables environmentVariables = from("sample-conf-files/with-typed-values.conf");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();

        assertThat(caps.getCapability("counts")).isInstanceOf(List.class);
        assertThat((List<Integer>) caps.getCapability("counts")).containsExactly(1,2,3);
    }

    @Test
    public void shouldSupportW3CProxySettings() {
        EnvironmentVariables environmentVariables = from("sample-conf-files/with-proxy.conf");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();

        assertThat(caps.getCapability("proxy")).isInstanceOf(Proxy.class);
        Proxy proxy = (Proxy) caps.getCapability("proxy");
        assertThat(proxy.getProxyType()).isEqualTo(Proxy.ProxyType.MANUAL);
        assertThat(proxy.getHttpProxy()).isEqualTo("myproxy.com:3128");
        assertThat(proxy.getFtpProxy()).isEqualTo("ftp.myproxy.com:3128");
        assertThat(proxy.getSocksProxy()).isEqualTo("socks.myproxy.com:3128");
        assertThat(proxy.getSocksVersion()).isEqualTo(1);
        assertThat(proxy.getSslProxy()).isEqualTo("ssl.myproxy.com:3128");
    }

    @Test
    public void shouldSupportIncompleteW3CProxySettings() {
        EnvironmentVariables environmentVariables = from("sample-conf-files/with-partial-proxy.conf");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();

        assertThat(caps.getCapability("proxy")).isInstanceOf(Proxy.class);
        Proxy proxy = (Proxy) caps.getCapability("proxy");
        assertThat(proxy.getProxyType()).isEqualTo(Proxy.ProxyType.MANUAL);
        assertThat(proxy.getHttpProxy()).isEqualTo("myproxy.com:3128");
    }


    @Test
    public void shouldSupportW3CTimeoutSettings() {
        EnvironmentVariables environmentVariables = from("sample-conf-files/with-timeouts.conf");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();

        Map<String,Object> timeouts = (Map<String,Object>) caps.getCapability("timeouts");
        assertThat(timeouts).hasFieldOrPropertyWithValue("script",20000);
        assertThat(timeouts).hasFieldOrPropertyWithValue("pageLoad",400000);
        assertThat(timeouts).hasFieldOrPropertyWithValue("implicit",1000);
    }

    @Test
    public void shouldSupportW3CScreenDimensions() {
        EnvironmentVariables environmentVariables = from("sample-conf-files/with-typed-values.conf");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();
        assertThat(caps.getCapability("screenResolution")).isEqualTo("1280x1024");
    }

    @Test
    public void shouldSupportSauceLabsOptions() {
        EnvironmentVariables environmentVariables = from("sample-conf-files/with-typed-values.conf");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();
        assertThat(caps.getCapability("screenResolution")).isEqualTo("1280x1024");
    }

    @Test
    public void shouldReadSauceLabsStringValues() {
        EnvironmentVariables environmentVariables = from("sample-conf-files/saucelabs.conf");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();
        Map<String,Object> sauceCaps = (Map<String,Object>) caps.getCapability("sauce:options");
        assertThat(sauceCaps.get("chromedriverVersion")).isEqualTo("88.0.4324.96");
    }

    @Test
    public void shouldReadSauceLabsIntValues() {
        EnvironmentVariables environmentVariables = from("sample-conf-files/saucelabs.conf");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();
        Map<String,Object> sauceCaps = (Map<String,Object>) caps.getCapability("sauce:options");
        assertThat(sauceCaps.get("maxDuration")).isEqualTo(10000);
    }

    @Test
    public void shouldReadSauceLabsListValues() {
        EnvironmentVariables environmentVariables = from("sample-conf-files/saucelabs.conf");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();
        Map<String,Object> sauceCaps = (Map<String,Object>) caps.getCapability("sauce:options");
        assertThat((List<String>)sauceCaps.get("tags")).containsExactly("red","green","blue");
    }

    @Test
    public void shouldReadSauceLabsMaps() {
        EnvironmentVariables environmentVariables = from("sample-conf-files/saucelabs.conf");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();
        Map<String,Object> sauceCaps = (Map<String,Object>) caps.getCapability("sauce:options");
        assertThat(((Map<String,Object>)sauceCaps.get("custom-data")).get("release")).isEqualTo("1.0");
    }

    @Test
    public void shouldReadChromeOptions() {
        Assume.assumeThat(System.getProperty("os.name").toLowerCase(), not(containsString("windows")));
        EnvironmentVariables environmentVariables = from("sample-conf-files/chrome.conf");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();
        Map<String,Object> chromeOptions = (Map<String,Object>) caps.getCapability("goog:chromeOptions");
        assertThat(((List<String>)chromeOptions.get("args"))).containsExactly("window-size=1000,800", "start-maximized");
        assertThat(chromeOptions.get("detach")).isEqualTo(true);
    }

    @Test
    public void shouldReadLocalStateAsMapsOfObjects() {
        Assume.assumeThat(System.getProperty("os.name").toLowerCase(), not(containsString("windows")));
        EnvironmentVariables environmentVariables = from("sample-conf-files/chrome/complete.conf");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();
        Map<String,Object> chromeOptions = (Map<String,Object>) caps.getCapability("goog:chromeOptions");
        Map<String,Object> localState =  (Map<String,Object>) chromeOptions.get("localState");
        assertThat(localState.get("quantity")).isEqualTo(1);
        assertThat(localState.get("color")).isEqualTo("red");
        assertThat(localState.get("max")).isEqualTo(false);
        assertThat((List<Integer>)localState.get("listOfValues")).containsExactly(1,2,3);
    }

    @Test
    public void shouldReadPrefsAsMapsOfObjects() {
        Assume.assumeThat(System.getProperty("os.name").toLowerCase(), not(containsString("windows")));
        EnvironmentVariables environmentVariables = from("sample-conf-files/chrome/complete.conf");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();
        Map<String,Object> chromeOptions = (Map<String,Object>) caps.getCapability("goog:chromeOptions");
        Map<String,Object> localState =  (Map<String,Object>) chromeOptions.get("localState");
        assertThat(localState.get("quantity")).isEqualTo(1);
        assertThat(localState.get("color")).isEqualTo("red");
        assertThat(localState.get("max")).isEqualTo(false);
        assertThat(localState.get("field.with.dots")).isEqualTo("value");
        assertThat((List<Integer>)localState.get("listOfValues")).containsExactly(1,2,3);
    }

    @Test
    public void shouldReadChromeOptionsWithNestedMaps() {
        Assume.assumeThat(System.getProperty("os.name").toLowerCase(), not(containsString("windows")));
        EnvironmentVariables environmentVariables = from("sample-conf-files/chrome.conf");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();
        Map<String,Object> chromeOptions = (Map<String,Object>) caps.getCapability("goog:chromeOptions");

        Map<String,Object> localState = (Map<String,Object>) chromeOptions.get("localState");
        assertThat(localState.get("quantity")).isEqualTo(1);
        assertThat(localState.get("color")).isEqualTo("red");
        assertThat((List<Integer>) localState.get("listOfValues")).containsExactly(1,2,3);
    }
    @Test
    public void shouldSubstituteSystemPropertyTokens() {
        Assume.assumeThat(System.getProperty("os.name").toLowerCase(), not(containsString("windows")));
        EnvironmentVariables environmentVariables = from("sample-conf-files/chrome.conf");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();
        Map<String,Object> chromeOptions = (Map<String,Object>) caps.getCapability("goog:chromeOptions");
        String homeDir = System.getenv("HOME");
        assertThat(chromeOptions.get("binary")).isEqualTo(homeDir + "/path/to/chromedriver");
    }
}
