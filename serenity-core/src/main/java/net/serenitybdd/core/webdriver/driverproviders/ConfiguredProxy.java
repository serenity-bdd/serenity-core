package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Proxy;

import java.util.Optional;

import static net.thucydides.core.ThucydidesSystemProperty.*;

public class ConfiguredProxy {

    public static Optional<Proxy> definedIn(EnvironmentVariables environmentVariables) {
        String httpProxy = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(SERENITY_PROXY_HTTP).orElse(null);
        String sslProxy = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(SERENITY_PROXY_SSL).orElse(null);
        String ftpProxy = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(SERENITY_PROXY_FTP).orElse(null);
        String noProxy = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(SERENITY_PROXY_NOPROXY).orElse(null);
        String proxyType = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(SERENITY_PROXY_TYPE).orElse(null);
        String proxyAutoconfigUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(SERENITY_PROXY_AUTOCONFIG).orElse(null);
        String socksProxy = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(SERENITY_PROXY_SOCKS_PROXY).orElse(null);
        String socksProxyUsername = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(SERENITY_PROXY_SOCKS_USERNAME).orElse(null);
        String socksProxyPassword = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(SERENITY_PROXY_SOCKS_PASSWORD).orElse(null);
        String socksProxyVersionValue = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(SERENITY_PROXY_SOCKS_VERSION).orElse(null);
        Integer socksProxyVersion = (StringUtils.isNumeric(socksProxyVersionValue)) ? Integer.parseInt(socksProxyVersionValue) : null;

        Boolean autodetect = EnvironmentSpecificConfiguration.from(environmentVariables).getBooleanProperty(ThucydidesSystemProperty.SERENITY_PROXY_AUTODETECT);

        if ((httpProxy  != null) && (!httpProxy.isEmpty())) {
            org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
            if (httpProxy != null) {
                proxy.setHttpProxy(httpProxy);
            }
            if (sslProxy != null) {
                proxy.setSslProxy(sslProxy);
            }
            if (ftpProxy != null) {
                proxy.setFtpProxy(ftpProxy);
            }
            proxy.setAutodetect(autodetect);

            if (ftpProxy != null) {
                proxy.setFtpProxy(ftpProxy);
            }
            if (noProxy != null) {
                proxy.setNoProxy(noProxy);
            }
            if (proxyAutoconfigUrl != null) {
                proxy.setProxyAutoconfigUrl(proxyAutoconfigUrl);
            }
            if (proxyType != null) {
                proxy.setProxyType(org.openqa.selenium.Proxy.ProxyType.valueOf(proxyType));
            }
            if (socksProxy != null) {
                proxy.setSocksProxy(socksProxy);
            }
            if (socksProxyUsername != null) {
                proxy.setSocksUsername(socksProxyUsername);
            }
            if (socksProxyPassword != null) {
                proxy.setSocksPassword(socksProxyPassword);
            }
            if (socksProxyVersion != 0) {
                proxy.setSocksVersion(socksProxyVersion);
            }
            return Optional.of(proxy);
        }
        return Optional.empty();
    }
}
