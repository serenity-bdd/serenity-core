package net.thucydides.core.webdriver.capabilities;

import org.openqa.selenium.Proxy;
import net.thucydides.core.webdriver.capabilities.InvalidCapabilityException;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Handles the configuration of proxy settings for Chrome/Chromium browsers.
 * This class transforms proxy configuration from a map format into Selenium's Proxy object.
 */
public class ChromeProxyConfigurator {

    public Proxy createProxyFromConfig(Map<?, ?> proxyConfig) {
        validateProxyConfig(proxyConfig);

        Proxy proxy = new Proxy();
        String proxyType = proxyConfig.get("proxyType").toString().toUpperCase();

        switch (proxyType) {
            case "MANUAL":
                configureManualProxy(proxyConfig, proxy);
                break;
            case "PAC":
                configurePacProxy(proxyConfig, proxy);
                break;
            case "DIRECT":
                proxy.setProxyType(Proxy.ProxyType.DIRECT);
                break;
            case "SYSTEM":
                proxy.setProxyType(Proxy.ProxyType.SYSTEM);
                break;
            case "AUTODETECT":
                proxy.setProxyType(Proxy.ProxyType.AUTODETECT);
                break;
            default:
                throw new InvalidCapabilityException("Invalid proxy type: " + proxyType);
        }

        configureNoProxyList(proxyConfig, proxy);

        return proxy;
    }

    private void validateProxyConfig(Map<?, ?> proxyConfig) {
        if (proxyConfig == null) {
            throw new InvalidCapabilityException("Proxy configuration cannot be null");
        }
        if (!proxyConfig.containsKey("proxyType")) {
            throw new InvalidCapabilityException("proxyType must be specified in proxy configuration");
        }
    }

    private void configureManualProxy(Map<?, ?> proxyConfig, Proxy proxy) {
        proxy.setProxyType(Proxy.ProxyType.MANUAL);
        boolean hasProxyConfig = false;

        if (proxyConfig.containsKey("httpProxy")) {
            String httpProxy = cleanProxyValue(proxyConfig.get("httpProxy").toString());
            if (!httpProxy.isEmpty()) {
                proxy.setHttpProxy(httpProxy);
                hasProxyConfig = true;
            }
        }

        if (proxyConfig.containsKey("sslProxy")) {
            String sslProxy = cleanProxyValue(proxyConfig.get("sslProxy").toString());
            if (!sslProxy.isEmpty()) {
                proxy.setSslProxy(sslProxy);
                hasProxyConfig = true;
            }
        }

        if (proxyConfig.containsKey("socksProxy")) {
            configureSocksProxy(proxyConfig, proxy);
            hasProxyConfig = true;
        }

        if (!hasProxyConfig) {
            throw new InvalidCapabilityException(
                    "Manual proxy configuration must include at least one of: httpProxy, sslProxy, or socksProxy");
        }
    }

    private void configureSocksProxy(Map<?, ?> proxyConfig, Proxy proxy) {
        String socksProxy = cleanProxyValue(proxyConfig.get("socksProxy").toString());
        if (!socksProxy.isEmpty()) {
            proxy.setSocksProxy(socksProxy);

            if (proxyConfig.containsKey("socksVersion")) {
                try {
                    String versionStr = proxyConfig.get("socksVersion").toString().trim();
                    if (!versionStr.isEmpty()) {
                        int socksVersion = Integer.parseInt(versionStr);
                        if (socksVersion != 4 && socksVersion != 5) {
                            throw new InvalidCapabilityException("SOCKS version must be either 4 or 5");
                        }
                        proxy.setSocksVersion(socksVersion);
                    }
                } catch (NumberFormatException e) {
                    throw new InvalidCapabilityException("Invalid SOCKS version number: " +
                            proxyConfig.get("socksVersion"));
                }
            }
        }
    }

    private void configurePacProxy(Map<?, ?> proxyConfig, Proxy proxy) {
        proxy.setProxyType(Proxy.ProxyType.PAC);
        if (!proxyConfig.containsKey("proxyAutoconfigUrl")) {
            throw new InvalidCapabilityException(
                    "PAC proxy configuration must include proxyAutoconfigUrl");
        }
        String pacUrl = cleanProxyValue(proxyConfig.get("proxyAutoconfigUrl").toString());
        if (pacUrl.isEmpty()) {
            throw new InvalidCapabilityException("proxyAutoconfigUrl cannot be empty");
        }
        proxy.setProxyAutoconfigUrl(pacUrl);
    }

    private void configureNoProxyList(Map<?, ?> proxyConfig, Proxy proxy) {
        if (proxyConfig.containsKey("noProxy") && proxy.getProxyType() == Proxy.ProxyType.MANUAL) {
            String noProxyList = proxyConfig.get("noProxy").toString();
            // Even if the list is empty, we should set it to maintain the empty string
            if (noProxyList.isEmpty()) {
                proxy.setNoProxy("");
            } else {
                // Clean up the no proxy list by trimming whitespace around entries
                String cleanedList = Arrays.stream(noProxyList.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.joining(","));
                proxy.setNoProxy(cleanedList);
            }
        }
    }

    private String cleanProxyValue(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }
}