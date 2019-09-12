package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.Proxy;

import java.util.Optional;

import static net.thucydides.core.ThucydidesSystemProperty.*;

public class ConfiguredProxy {

    public static Optional<Proxy> definedIn(EnvironmentVariables environmentVariables) {
        String httpProxy = ThucydidesSystemProperty.SERENITY_PROXY_HTTP.from(environmentVariables);
        String sslProxy = ThucydidesSystemProperty.SERENITY_PROXY_SSL.from(environmentVariables);
        String ftpProxy = ThucydidesSystemProperty.SERENITY_PROXY_FTP.from(environmentVariables);
        String noProxy = ThucydidesSystemProperty.SERENITY_PROXY_NOPROXY.from(environmentVariables);
        String proxyType = SERENITY_PROXY_TYPE.from(environmentVariables);
        String proxyAutoconfigUrl = ThucydidesSystemProperty.SERENITY_PROXY_AUTOCONFIG.from(environmentVariables);
        String socksProxy = SERENITY_PROXY_SOCKS_PROXY.from(environmentVariables);
        String socksProxyUsername = SERENITY_PROXY_SOCKS_USERNAME.from(environmentVariables);
        String socksProxyPassword = SERENITY_PROXY_SOCKS_PASSWORD.from(environmentVariables);
        Integer socksProxyVersion = SERENITY_PROXY_SOCKS_VERSION.integerFrom(environmentVariables);

        Boolean autodetect = ThucydidesSystemProperty.SERENITY_PROXY_AUTODETECT.booleanFrom(environmentVariables, false);

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
