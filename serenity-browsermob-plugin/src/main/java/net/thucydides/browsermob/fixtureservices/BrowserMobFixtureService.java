package net.thucydides.browsermob.fixtureservices;

import com.google.common.base.Splitter;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.fixtureservices.FixtureException;
import net.thucydides.core.fixtureservices.FixtureService;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;
import java.util.Optional;

import static net.thucydides.browsermob.fixtureservices.BrowserMobSystemProperties.BROWSER_MOB_PROXY;
import static net.thucydides.core.webdriver.WebDriverFactory.getDriverFrom;

public class BrowserMobFixtureService implements FixtureService {

    public static final int DEFAULT_PORT = 5555;
    private static final int MIN_AVAILABLE_PORT = 49152;

    private final EnvironmentVariables environmentVariables;
    private Ports ports;

    private ThreadLocal<BrowserMobProxy> threadLocalproxyServer = new  ThreadLocal<>();

    public BrowserMobFixtureService() {
        this(Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
    }

    public BrowserMobFixtureService(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        ports = new Ports(defaultPortDefinedIn(environmentVariables));
    }

    public BrowserMobFixtureService(EnvironmentVariables environmentVariables, Ports ports) {
        this.environmentVariables = environmentVariables;
        this.ports = ports;
    }

    @Override
    public void setup() throws FixtureException {
        if (useBrowserMobProxyManager()) {
            try {
                initializeProxy(getAvailablePort());
            } catch (Exception e) {
                throw new FixtureException("Failed to initialize proxy", e);
            }
        }
    }

    public BrowserMobProxy getProxyServer() {
        return getBrowserMobProxy();
    }

    private void initializeProxy(int port) throws Exception {
        boolean refuseUntrustedCertificates = ThucydidesSystemProperty.REFUSE_UNTRUSTED_CERTIFICATES.booleanFrom(environmentVariables, false);
        threadLocalproxyServer.set(new BrowserMobProxyServer());

        getBrowserMobProxy().setTrustAllServers(!refuseUntrustedCertificates);
        getBrowserMobProxy().start(port);
    }

    private BrowserMobProxy getBrowserMobProxy() {
        return threadLocalproxyServer.get();
    }

    @Override
    public void shutdown() {
        if (getBrowserMobProxy() != null) {
            try {
                getBrowserMobProxy().stop();
            } catch (Exception e) {
                throw new FixtureException("Could not shut down BrowserMob proxy", e);
            }
            threadLocalproxyServer.remove();
        }
    }

    @Override
    public void addCapabilitiesTo(MutableCapabilities capabilities) {
        if (!proxyServerRunning()) {
            setup();
        }
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(getBrowserMobProxy());
        capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
    }

    private boolean proxyServerRunning() {
        return (getBrowserMobProxy() != null);
    }

    private boolean useBrowserMobProxyManager() {
        String browserMobFilter = EnvironmentSpecificConfiguration.from(environmentVariables)
                                        .getOptionalProperty(BrowserMobSystemProperties.BROWSER_MOB_FILTER.toString())
                                        .orElse(null);
//        String browserMobFilter = environmentVariables.getProperty(BrowserMobSystemProperties.BROWSER_MOB_FILTER);
        return (StringUtils.isEmpty(browserMobFilter) || shouldActivateBrowserMobWithDriver(browserMobFilter, environmentVariables));
    }

    private boolean shouldActivateBrowserMobWithDriver(String filter, EnvironmentVariables environmentVariables) {
        String currentDriver = getDriverFrom(environmentVariables);
        List allowedBrowsers = Splitter.on(",").trimResults().splitToList(filter.toLowerCase());
        return StringUtils.isEmpty(currentDriver) || allowedBrowsers.contains(currentDriver.toLowerCase());
    }

    public int getPort() {
        return (threadLocalproxyServer.get() != null) ? threadLocalproxyServer.get().getPort() : 0;
    }

    protected int getAvailablePort() {
//        int defaultPort = portDefinedIn(environmentVariables.getPropertyAsInteger(BROWSER_MOB_PROXY, DEFAULT_PORT);
        int defaultPort = portDefinedIn(environmentVariables, BROWSER_MOB_PROXY);
        if (ports.isAvailable(defaultPort)) {
            return defaultPort;
        } else {
            return ports.nextAvailablePort(MIN_AVAILABLE_PORT);
        }
    }

    private int defaultPortDefinedIn(EnvironmentVariables environmentVariables) {
        return portDefinedIn(environmentVariables, BROWSER_MOB_PROXY);
//        return environmentVariables.getPropertyAsInteger(BROWSER_MOB_PROXY, DEFAULT_PORT);
    }

    private Integer portDefinedIn(EnvironmentVariables environmentVariables, BrowserMobSystemProperties propertyName) {
        Optional<String> definedPort = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(propertyName.toString());
        return definedPort.map(Integer::parseInt).orElse(DEFAULT_PORT);
    }

}
