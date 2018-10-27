package net.serenitybdd.core.webdriver.driverproviders;

import com.google.gson.JsonObject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class SetProxyConfiguration {
    private final EnvironmentVariables environmentVariables;

    public SetProxyConfiguration(EnvironmentVariables environmentVariables) {

        this.environmentVariables = environmentVariables;
    }

    public static SetProxyConfiguration from(EnvironmentVariables environmentVariables) {
        return new SetProxyConfiguration(environmentVariables);
    }

    public void in(DesiredCapabilities capabilities) {
        String proxyUrl = ThucydidesSystemProperty.SERENITY_PROXY_HTTP.from(environmentVariables);
        String proxyPort = ThucydidesSystemProperty.SERENITY_PROXY_HTTP_PORT.from(environmentVariables);
        String sslProxy = ThucydidesSystemProperty.SERENITY_PROXY_SSL.from(environmentVariables, proxyUrl);
        String sslProxyPort = ThucydidesSystemProperty.SERENITY_PROXY_SSL_PORT.from(environmentVariables);

        Proxy proxy = new Proxy();

        if ((proxyUrl  != null) && (!proxyUrl.isEmpty())) {
            JsonObject json = new JsonObject();
            if (StringUtils.isNotEmpty(proxyUrl)) {
                proxy.setHttpProxy(proxyUrl + ":"+ proxyPort);
            }
            if (StringUtils.isNotEmpty(sslProxy)) {
                proxy.setSslProxy(sslProxy + ":"+ sslProxyPort);
            }
            if (StringUtils.isNotEmpty(sslProxyPort)) {
                json.addProperty("sslProxyPort", sslProxyPort);
            }
            capabilities.setCapability(CapabilityType.PROXY, proxy);
        }
    }
}
