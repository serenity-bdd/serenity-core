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

public class WhenSettingW3CPropertiesInTheWebdriverSection {

    EnvironmentVariables environmentVariables;

    @Before
    public void setupEnvironment() {
        environmentVariables = new MockEnvironmentVariables();
    }

    @Test
    public void shouldSetBrowserNameAndVersionAndOS() {
        environmentVariables.setProperty("webdriver.browserName","Chrome");
        environmentVariables.setProperty("webdriver.browserVersion","80");
        environmentVariables.setProperty("webdriver.platformName","macos 10.6");

        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver");

        assertThat(caps.getBrowserName()).isEqualTo("Chrome");
        assertThat(caps.getBrowserVersion()).isEqualTo("80");
        assertThat(caps.getPlatformName()).isEqualTo(Platform.SNOW_LEOPARD);
    }

    @Test
    public void shouldWorkWithEnvironmentSpecificConfigs() {
        environmentVariables.setProperty("environments.mac.webdriver.browserName","Chrome");
        environmentVariables.setProperty("environments.mac.webdriver.browserVersion","80");
        environmentVariables.setProperty("environments.mac.webdriver.platformName","macos 10.6");
        environmentVariables.setProperty("environment","mac");

        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver");

        assertThat(caps.getBrowserName()).isEqualTo("Chrome");
        assertThat(caps.getBrowserVersion()).isEqualTo("80");
        assertThat(caps.getPlatformName()).isEqualTo(Platform.SNOW_LEOPARD);
    }

    @Test
    public void shouldSupportW3CStringProperties() {
        environmentVariables.setProperty("webdriver.pageLoadStrategy","eager");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver");
        assertThat(caps.getCapability("pageLoadStrategy")).isEqualTo("eager");
    }

    @Test
    public void shouldSupportW3CStringPropertiesThatLookLikeNumbers() {
        environmentVariables.setProperty("webdriver.chromedriverVersion","88.0.4324.27");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver");
        assertThat(caps.getCapability("chromedriverVersion")).isEqualTo("88.0.4324.27");
    }

    @Test
    public void shouldSupportW3CScreenDimensions() {
        environmentVariables.setProperty("webdriver.screenResolution","1280x1024");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver");
        assertThat(caps.getCapability("screenResolution")).isEqualTo("1280x1024");
    }

    @Test
    public void shouldSupportW3CBooleanProperties() {
        environmentVariables.setProperty("webdriver.acceptInsecureCerts","true");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver");
        assertThat(caps.getCapability("acceptInsecureCerts")).isEqualTo(true);
    }

    @Test
    public void shouldSupportW3CIntegerProperties() {
        environmentVariables.setProperty("webdriver.maxDuration","1800");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver");
        assertThat(caps.getCapability("maxDuration")).isEqualTo(1800);
    }

    @Test
    public void shouldSupportW3CListProperties() {
        environmentVariables.setProperty("webdriver.tags", "[\"tag1\",\"tag2\",\"tag3\"]");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver");
        assertThat(caps.getCapability("tags")).isInstanceOf(List.class);
        assertThat((List)caps.getCapability("tags")).containsExactly("tag1","tag2","tag3");
    }


    @Test
    public void shouldSupportW3CProxy() {
        environmentVariables.setProperty("webdriver.proxy", "{\"proxyType\": \"manual\",\n" +
                                                            "\"httpProxy\": \"myproxy.com:3128\"}");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver");

        assertThat(caps.getCapability("proxy")).isInstanceOf(Proxy.class);
        Proxy proxy = (Proxy) caps.getCapability("proxy");
        assertThat(proxy.getProxyType()).isEqualTo(Proxy.ProxyType.MANUAL);
        assertThat(proxy.getHttpProxy()).isEqualTo("myproxy.com:3128");
    }

    @Test
    public void shouldSupportW3CTimeouts() {
        environmentVariables.setProperty("webdriver.timeouts", "{\"script\": 20000,\n" +
                                                               "             \"pageLoad\": 400000,\n" +
                                                               "             \"implicit\": 1000}");
        DesiredCapabilities caps = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver");

        Map timeouts = (Map) caps.getCapability("timeouts");
        assertThat(timeouts).hasFieldOrPropertyWithValue("script",20000);
        assertThat(timeouts).hasFieldOrPropertyWithValue("pageLoad",400000);
        assertThat(timeouts).hasFieldOrPropertyWithValue("implicit",1000);
    }
}
