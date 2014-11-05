package net.thucydides.core.webdriver.phantomjs;

import com.google.common.collect.Lists;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.ArrayList;

public class PhantomJSCapabilityEnhancer {

    private final EnvironmentVariables environmentVariables;

    public PhantomJSCapabilityEnhancer(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public void enhanceCapabilities(DesiredCapabilities capabilities) {
        if (environmentVariables.getProperty(ThucydidesSystemProperty.PHANTOMJS_BINARY_PATH) != null) {
            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                    environmentVariables.getProperty(ThucydidesSystemProperty.PHANTOMJS_BINARY_PATH));
        }

        ArrayList<String> cliArgs = Lists.newArrayList();
        setSecurityOptions(cliArgs);
        setLoggingOptions(cliArgs);
        if (StringUtils.isNotEmpty(ThucydidesSystemProperty.THUCYDIDES_PROXY_HTTP.from(environmentVariables))) {
            setProxyOptions(cliArgs);
        }
        if (StringUtils.isNotEmpty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL.from(environmentVariables))) {
            setRemoteOptions(cliArgs);
        }
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgs.toArray(new String[]{}));
    }

    private void setRemoteOptions(ArrayList<String> cliArgs) {
        cliArgs.add("--webdriver-selenium-grid-hub=" + ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL.from(environmentVariables));
        if (StringUtils.isNotEmpty(ThucydidesSystemProperty.PHANTOMJS_WEBDRIVER_PORT.from(environmentVariables))) {
            cliArgs.add("--webdriver=" + ThucydidesSystemProperty.PHANTOMJS_WEBDRIVER_PORT.from(environmentVariables));
        }
    }

    private void setProxyOptions(ArrayList<String> cliArgs) {
        String proxyUrl = ThucydidesSystemProperty.THUCYDIDES_PROXY_HTTP.from(environmentVariables);
        String proxyPort = ThucydidesSystemProperty.THUCYDIDES_PROXY_HTTP_PORT.from(environmentVariables);
        String proxyType = ThucydidesSystemProperty.THUCYDIDES_PROXY_TYPE.from(environmentVariables);
        String proxyUser = ThucydidesSystemProperty.THUCYDIDES_PROXY_USER.from(environmentVariables);
        String proxyPassword = ThucydidesSystemProperty.THUCYDIDES_PROXY_PASSWORD.from(environmentVariables);
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
        cliArgs.add("--ssl-protocol=any");
        cliArgs.add("--ignore-ssl-errors=true");
    }

    private void setLoggingOptions(ArrayList<String> cliArgs ) {
        // TODO: Parameterize
        cliArgs.add("--webdriver-loglevel=OFF");
    }
}
