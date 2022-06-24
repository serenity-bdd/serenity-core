package net.thucydides.core.webdriver.capabilities;

import net.thucydides.core.environment.MockEnvironmentVariables;
import net.thucydides.core.util.EnvironmentVariables;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenSettingW3CPropertiesInTheW3CSection {

    EnvironmentVariables environmentVariables;

    @Before
    public void setupEnvironment() {
        environmentVariables = new MockEnvironmentVariables();
    }

    @Test
    public void shouldSetBrowserNameAndVersionAndOS() {
        environmentVariables.setProperty("w3c.browserName","Chrome");
        environmentVariables.setProperty("w3c.browserVersion","80");
        environmentVariables.setProperty("w3c.platformName","macos 10.6");

        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("w3c");

        assertThat(caps.getBrowserName()).isEqualTo("Chrome");
        assertThat(caps.getBrowserVersion()).isEqualTo("80");
        assertThat(caps.getPlatformName()).isEqualTo(Platform.SNOW_LEOPARD);
    }

    @Test
    public void shouldWorkWithEnvironmentSpecificConfigs() {
        environmentVariables.setProperty("environments.mac.w3c.browserName","Chrome");
        environmentVariables.setProperty("environments.mac.w3c.browserVersion","80");
        environmentVariables.setProperty("environments.mac.w3c.platformName","macos 10.6");
        environmentVariables.setProperty("environment","mac");

        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("w3c");

        assertThat(caps.getBrowserName()).isEqualTo("Chrome");
        assertThat(caps.getBrowserVersion()).isEqualTo("80");
        assertThat(caps.getPlatformName()).isEqualTo(Platform.SNOW_LEOPARD);
    }

    @Test
    public void shouldSupportW3CStringProperties() {
        environmentVariables.setProperty("w3c.pageLoadStrategy","eager");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("w3c");
        assertThat(caps.getCapability("pageLoadStrategy")).isEqualTo("eager");
    }

    @Test
    public void shouldSupportW3CStringPropertiesThatLookLikeNumbers() {
        environmentVariables.setProperty("w3c.chromedriverVersion","88.0.4324.27");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("w3c");
        assertThat(caps.getCapability("chromedriverVersion")).isEqualTo("88.0.4324.27");
    }

    @Test
    public void shouldSupportW3CScreenDimensions() {
        environmentVariables.setProperty("w3c.screenResolution","1280x1024");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("w3c");
        assertThat(caps.getCapability("screenResolution")).isEqualTo("1280x1024");
    }

    @Test
    public void shouldSupportW3CBooleanProperties() {
        environmentVariables.setProperty("w3c.acceptInsecureCerts","true");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("w3c");
        assertThat(caps.getCapability("acceptInsecureCerts")).isEqualTo(true);
    }

    @Test
    public void shouldSupportW3CIntegerProperties() {
        environmentVariables.setProperty("w3c.maxDuration","1800");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("w3c");
        assertThat(caps.getCapability("maxDuration")).isEqualTo(1800);
    }

    @Test
    public void shouldSupportW3CListProperties() {
        environmentVariables.setProperty("w3c.tags", "[\"tag1\",\"tag2\",\"tag3\"]");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("w3c");
        assertThat(caps.getCapability("tags")).isInstanceOf(List.class);
        assertThat((List)caps.getCapability("tags")).containsExactly("tag1","tag2","tag3");
    }


    @Test
    public void shouldSupportW3CProxy() {
        environmentVariables.setProperty("w3c.proxy", "{\"proxyType\": \"manual\",\n" +
                                                       "\"httpProxy\": \"myproxy.com:3128\"," +
                                                       "\"ftpProxy\": \"ftp.myproxy.com:3128\"," +
                                                       "\"sslProxy\": \"ssl.myproxy.com:3128\"," +
                                                       "\"socksProxy\": \"socks.myproxy.com:3128\"," +
                                                       "\"socksVersion\": 1" +
                                                       "}");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("w3c");

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
    public void shouldSupportW3CTimeouts() {
        environmentVariables.setProperty("w3c.timeouts", "{\"script\": 20000,\n" +
                                         "                 \"pageLoad\": 400000,\n" +
                                         "                 \"implicit\": 1000}");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("w3c");

        Map timeouts = (Map) caps.getCapability("timeouts");
        assertThat(timeouts).hasFieldOrPropertyWithValue("script",20000);
        assertThat(timeouts).hasFieldOrPropertyWithValue("pageLoad",400000);
        assertThat(timeouts).hasFieldOrPropertyWithValue("implicit",1000);
    }
}
