package net.thucydides.core.webdriver.capabilities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Proxy;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Chrome Proxy Configuration")
class ChromeProxyConfiguratorTest {

    private final ChromeProxyConfigurator configurator = new ChromeProxyConfigurator();

    @Nested
    @DisplayName("Input validation")
    class InputValidation {

        @Test
        @DisplayName("should reject null proxy configuration")
        void rejectsNullConfig() {
            assertThatThrownBy(() -> configurator.createProxyFromConfig(null))
                    .isInstanceOf(InvalidCapabilityException.class)
                    .hasMessage("Proxy configuration cannot be null");
        }

        @Test
        @DisplayName("should require proxy type to be specified")
        void requiresProxyType() {
            Map<String, String> config = new HashMap<>();

            assertThatThrownBy(() -> configurator.createProxyFromConfig(config))
                    .isInstanceOf(InvalidCapabilityException.class)
                    .hasMessage("proxyType must be specified in proxy configuration");
        }

        @Test
        @DisplayName("should reject invalid proxy type")
        void rejectsInvalidProxyType() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "INVALID");

            assertThatThrownBy(() -> configurator.createProxyFromConfig(config))
                    .isInstanceOf(InvalidCapabilityException.class)
                    .hasMessage("Invalid proxy type: INVALID");
        }
    }

    @Nested
    @DisplayName("Manual proxy configuration")
    class ManualProxyConfiguration {

        @Test
        @DisplayName("should configure HTTP proxy")
        void configuresHttpProxy() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "MANUAL");
            config.put("httpProxy", "http-proxy.example.com:8080");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getProxyType()).isEqualTo(Proxy.ProxyType.MANUAL);
            assertThat(proxy.getHttpProxy()).isEqualTo("http-proxy.example.com:8080");
        }

        @Test
        @DisplayName("should configure HTTPS proxy")
        void configuresHttpsProxy() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "MANUAL");
            config.put("sslProxy", "https-proxy.example.com:8443");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getProxyType()).isEqualTo(Proxy.ProxyType.MANUAL);
            assertThat(proxy.getSslProxy()).isEqualTo("https-proxy.example.com:8443");
        }

        @Test
        @DisplayName("should configure SOCKS proxy")
        void configuresSocksProxy() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "MANUAL");
            config.put("socksProxy", "socks.example.com:1080");
            config.put("socksVersion", "5");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getProxyType()).isEqualTo(Proxy.ProxyType.MANUAL);
            assertThat(proxy.getSocksProxy()).isEqualTo("socks.example.com:1080");
            assertThat(proxy.getSocksVersion()).isEqualTo(5);
        }

        @Test
        @DisplayName("should reject invalid SOCKS version")
        void rejectsInvalidSocksVersion() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "MANUAL");
            config.put("socksProxy", "socks.example.com:1080");
            config.put("socksVersion", "6");

            assertThatThrownBy(() -> configurator.createProxyFromConfig(config))
                    .isInstanceOf(InvalidCapabilityException.class)
                    .hasMessage("SOCKS version must be either 4 or 5");
        }

        @Test
        @DisplayName("should require at least one proxy type for manual configuration")
        void requiresAtLeastOneProxyType() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "MANUAL");

            assertThatThrownBy(() -> configurator.createProxyFromConfig(config))
                    .isInstanceOf(InvalidCapabilityException.class)
                    .hasMessage("Manual proxy configuration must include at least one of: httpProxy, sslProxy, or socksProxy");
        }

        @Test
        @DisplayName("should configure multiple proxy types together")
        void configuresMultipleProxyTypes() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "MANUAL");
            config.put("httpProxy", "http-proxy.example.com:8080");
            config.put("sslProxy", "https-proxy.example.com:8443");
            config.put("socksProxy", "socks.example.com:1080");
            config.put("socksVersion", "4");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getProxyType()).isEqualTo(Proxy.ProxyType.MANUAL);
            assertThat(proxy.getHttpProxy()).isEqualTo("http-proxy.example.com:8080");
            assertThat(proxy.getSslProxy()).isEqualTo("https-proxy.example.com:8443");
            assertThat(proxy.getSocksProxy()).isEqualTo("socks.example.com:1080");
            assertThat(proxy.getSocksVersion()).isEqualTo(4);
        }
    }

    @Nested
    @DisplayName("PAC proxy configuration")
    class PacProxyConfiguration {

        @Test
        @DisplayName("should configure PAC proxy with auto-config URL")
        void configuresPacProxy() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "PAC");
            config.put("proxyAutoconfigUrl", "http://proxy.example.com/proxy.pac");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getProxyType()).isEqualTo(Proxy.ProxyType.PAC);
            assertThat(proxy.getProxyAutoconfigUrl())
                    .isEqualTo("http://proxy.example.com/proxy.pac");
        }

        @Test
        @DisplayName("should require auto-config URL for PAC proxy")
        void requiresAutoConfigUrl() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "PAC");

            assertThatThrownBy(() -> configurator.createProxyFromConfig(config))
                    .isInstanceOf(InvalidCapabilityException.class)
                    .hasMessage("PAC proxy configuration must include proxyAutoconfigUrl");
        }
    }

    @Nested
    @DisplayName("Simple proxy types")
    class SimpleProxyTypes {

        @Test
        @DisplayName("should configure DIRECT proxy")
        void configuresDirectProxy() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "DIRECT");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getProxyType()).isEqualTo(Proxy.ProxyType.DIRECT);
        }

        @Test
        @DisplayName("should configure SYSTEM proxy")
        void configuresSystemProxy() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "SYSTEM");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getProxyType()).isEqualTo(Proxy.ProxyType.SYSTEM);
        }

        @Test
        @DisplayName("should configure AUTODETECT proxy")
        void configuresAutodetectProxy() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "AUTODETECT");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getProxyType()).isEqualTo(Proxy.ProxyType.AUTODETECT);
        }
    }

    @Nested
    @DisplayName("No proxy list configuration")
    class NoProxyConfiguration {

        @Test
        @DisplayName("should configure no proxy list")
        void configuresNoProxyList() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "MANUAL");
            config.put("httpProxy", "proxy.example.com:8080");
            config.put("noProxy", "localhost,127.0.0.1,.example.com");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getNoProxy()).isEqualTo("localhost,127.0.0.1,.example.com");
        }

        @Test
        @DisplayName("should allow no proxy list with manual proxy")
        void allowsNoProxyWithManualProxy() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "MANUAL");
            config.put("httpProxy", "proxy.example.com:8080");
            config.put("noProxy", "localhost,127.0.0.1");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getProxyType()).isEqualTo(Proxy.ProxyType.MANUAL);
            assertThat(proxy.getNoProxy()).isEqualTo("localhost,127.0.0.1");
        }

        @Test
        @DisplayName("should ignore no proxy list for non-manual proxy types")
        void ignoresNoProxyForNonManualTypes() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "SYSTEM");
            config.put("noProxy", "localhost,127.0.0.1");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getProxyType()).isEqualTo(Proxy.ProxyType.SYSTEM);
            // No proxy list should be ignored for SYSTEM proxy type
            assertThat(proxy.getNoProxy()).isNull();
        }
    }

    @Nested
    @DisplayName("Proxy format validation")
    class ProxyFormatValidation {

        @Test
        @DisplayName("should accept proxy addresses with ports")
        void acceptsProxyAddressesWithPorts() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "MANUAL");
            config.put("httpProxy", "proxy.example.com:8080");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getHttpProxy()).isEqualTo("proxy.example.com:8080");
        }

        @Test
        @DisplayName("should accept proxy addresses without ports")
        void acceptsProxyAddressesWithoutPorts() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "MANUAL");
            config.put("httpProxy", "proxy.example.com");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getHttpProxy()).isEqualTo("proxy.example.com");
        }

        @Test
        @DisplayName("should accept IPv4 addresses as proxy")
        void acceptsIpv4Addresses() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "MANUAL");
            config.put("httpProxy", "192.168.1.1:8080");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getHttpProxy()).isEqualTo("192.168.1.1:8080");
        }

        @Test
        @DisplayName("should accept IPv6 addresses as proxy")
        void acceptsIpv6Addresses() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "MANUAL");
            config.put("httpProxy", "[2001:db8::1]:8080");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getHttpProxy()).isEqualTo("[2001:db8::1]:8080");
        }
    }

    @Nested
    @DisplayName("Case sensitivity handling")
    class CaseSensitivityHandling {

        @Test
        @DisplayName("should accept lowercase proxy type")
        void acceptsLowercaseProxyType() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "manual");
            config.put("httpProxy", "proxy.example.com:8080");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getProxyType()).isEqualTo(Proxy.ProxyType.MANUAL);
        }

        @Test
        @DisplayName("should accept mixed case proxy type")
        void acceptsMixedCaseProxyType() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "MaNuAl");
            config.put("httpProxy", "proxy.example.com:8080");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getProxyType()).isEqualTo(Proxy.ProxyType.MANUAL);
        }
    }

    @Nested
    @DisplayName("Authentication handling")
    class AuthenticationHandling {

        @Test
        @DisplayName("should handle proxy with authentication credentials")
        void handlesProxyAuthentication() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "MANUAL");
            config.put("httpProxy", "user:pass@proxy.example.com:8080");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getHttpProxy()).isEqualTo("user:pass@proxy.example.com:8080");
        }
    }

    @Nested
    @DisplayName("Real-world configurations")
    class RealWorldConfigurations {

        @Test
        @DisplayName("should handle corporate proxy setup")
        void handlesCorporateProxySetup() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "MANUAL");
            config.put("httpProxy", "proxy.corporate.com:8080");
            config.put("sslProxy", "proxy.corporate.com:8443");
            config.put("noProxy", "*.corporate.com,localhost,127.0.0.1,10.*");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getHttpProxy()).isEqualTo("proxy.corporate.com:8080");
            assertThat(proxy.getSslProxy()).isEqualTo("proxy.corporate.com:8443");
            assertThat(proxy.getNoProxy()).isEqualTo("*.corporate.com,localhost,127.0.0.1,10.*");
        }

        @Test
        @DisplayName("should handle cloud proxy setup")
        void handlesCloudProxySetup() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "PAC");
            config.put("proxyAutoconfigUrl", "https://cloud-proxy.company.com/proxy.pac");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getProxyAutoconfigUrl())
                    .isEqualTo("https://cloud-proxy.company.com/proxy.pac");
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("should handle empty string values")
        void handlesEmptyStringValues() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "MANUAL");
            config.put("httpProxy", "proxy.example.com:8080");
            config.put("noProxy", "");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getNoProxy()).isEmpty();
        }

        @Test
        @DisplayName("should handle multiple comma-separated proxies")
        void handlesMultipleProxies() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "MANUAL");
            config.put("httpProxy", "proxy1.example.com:8080,proxy2.example.com:8080");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getHttpProxy())
                    .isEqualTo("proxy1.example.com:8080,proxy2.example.com:8080");
        }

        @Test
        @DisplayName("should handle whitespace in proxy values")
        void handlesWhitespace() {
            Map<String, String> config = new HashMap<>();
            config.put("proxyType", "MANUAL");
            config.put("httpProxy", " proxy.example.com:8080 ");
            config.put("noProxy", " localhost , 127.0.0.1 ");

            Proxy proxy = configurator.createProxyFromConfig(config);

            assertThat(proxy.getHttpProxy()).isEqualTo("proxy.example.com:8080");
            assertThat(proxy.getNoProxy()).isEqualTo("localhost,127.0.0.1");
        }
    }

}