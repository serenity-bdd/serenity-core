package net.thucydides.core.webdriver.phantomjs;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.AddCustomCapabilities;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.ArrayList;

import static net.thucydides.core.ThucydidesSystemProperty.*;
import static org.openqa.selenium.phantomjs.PhantomJSDriverService.PHANTOMJS_CLI_ARGS;

public class PhantomJSCapabilityEnhancer {

    private final EnvironmentVariables environmentVariables;

    public PhantomJSCapabilityEnhancer(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public void enhanceCapabilities(DesiredCapabilities capabilities) {
        if (PHANTOMJS_BINARY_PATH.from(environmentVariables) != null) {
            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                                       PHANTOMJS_BINARY_PATH.from(environmentVariables));
        }

        ArrayList<String> cliArgs = new ArrayList<>();
        setSecurityOptions(cliArgs);
        setLoggingOptions(cliArgs);

        if (StringUtils.isNotEmpty(SERENITY_PROXY_HTTP.from(environmentVariables))) {
            setProxyOptions(cliArgs);
        }
        if (StringUtils.isNotEmpty(WEBDRIVER_REMOTE_URL.from(environmentVariables))) {
            setRemoteOptions(cliArgs);
        }
        if (StringUtils.isNotEmpty(PHANTOMJS_SSL_PROTOCOL.from(environmentVariables))) {
            String sslSupport = PHANTOMJS_SSL_PROTOCOL.from(environmentVariables);
            if (sslSupport.equals("sslv2") ||
                    sslSupport.equals("sslv3") ||
                    sslSupport.equals("tlsv1") ||
                    sslSupport.equals("any")) {
                cliArgs.add("--ssl-protocol=" + sslSupport);
            }
            else {
                cliArgs.add("--ssl-protocol=any");
            }
        }
        else {
            cliArgs.add("--ssl-protocol=any");
        }

        AddCustomCapabilities.startingWith("phantomjs.").from(environmentVariables).withAndWithoutPrefixes().to(capabilities);

        capabilities.setCapability(PHANTOMJS_CLI_ARGS, cliArgs.toArray(new String[]{}));
    }

    private void setRemoteOptions(ArrayList<String> cliArgs) {
        cliArgs.add("--webdriver-selenium-grid-hub=" + WEBDRIVER_REMOTE_URL.from(environmentVariables));
        if (StringUtils.isNotEmpty(ThucydidesSystemProperty.PHANTOMJS_WEBDRIVER_PORT.from(environmentVariables))) {
            cliArgs.add("--webdriver=" + ThucydidesSystemProperty.PHANTOMJS_WEBDRIVER_PORT.from(environmentVariables));
        }
    }

    private void setProxyOptions(ArrayList<String> cliArgs) {
        String proxyUrl = SERENITY_PROXY_HTTP.from(environmentVariables);
        String proxyPort = ThucydidesSystemProperty.SERENITY_PROXY_HTTP_PORT.from(environmentVariables);
        String proxyType = ThucydidesSystemProperty.SERENITY_PROXY_TYPE.from(environmentVariables);
        String proxyUser = ThucydidesSystemProperty.SERENITY_PROXY_USER.from(environmentVariables);
        String proxyPassword = ThucydidesSystemProperty.SERENITY_PROXY_PASSWORD.from(environmentVariables);
        if (StringUtils.isEmpty(proxyPort)) {
            cliArgs.add("--proxy=" + proxyUrl);
        } else {
            cliArgs.add("--proxy=" + proxyUrl + ":" + proxyPort);
        }
        if (StringUtils.isNotEmpty(proxyUser)) {
            cliArgs.add("--proxy-auth=" + proxyUser + ":" + proxyPassword);
        }
        if (StringUtils.isNotEmpty(proxyType)) {
            cliArgs.add("--proxy-type=" + proxyType);
        }
    }

    /*
            given:
            environmentVariables.setProperty("webdriver.remote.url","http://127.0.0.1:4444")
            environmentVariables.setProperty("phantomjs.webdriver.port","5555")

            def enhancer = new PhantomJSCapabilityEnhancer(environmentVariables)
        when:
            enhancer.enhanceCapabilities(capabilities)
        then:
            1 * capabilities.setCapability("phantomjs.cli.args",
                    ['--web-security=false',
                        '--ssl-protocol=any',
                        '--ignore-ssl-errors=true',
                        '--webdriver=8080',
                        '--webdriver-selenium-grid-hub=http://127.0.0.1:4444'
     */

    private void setSecurityOptions(ArrayList<String> cliArgs ) {
        cliArgs.add("--web-security=false");
        cliArgs.add("--ignore-ssl-errors=true");
    }

    private void setLoggingOptions(ArrayList<String> cliArgs ) {
        // TODO: Parameterize
        cliArgs.add("--webdriver-loglevel=OFF");
    }
}
