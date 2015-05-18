package net.thucydides.core.webdriver.capabilities;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Provides BrowserStack specific capabilities
 *
 * @author Imran Khan
 */

public class BrowserStackRemoteDriverCapabilities implements RemoteDriverCapabilities {

    private final EnvironmentVariables environmentVariables;

    public BrowserStackRemoteDriverCapabilities(EnvironmentVariables environmentVariables){
        this.environmentVariables = environmentVariables;
    }

    @Override
    public String getUrl() {
        return ThucydidesSystemProperty.BROWSERSTACK_URL.from(environmentVariables);
    }

    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {

        configureBrowserStackOs(capabilities);

        configureBrowserStackOsVersion(capabilities);

        configureBrowserStackBrowser(capabilities);

        configureBrowserStackBrowserVersion(capabilities);

        configureBrowserStackDevice(capabilities);

        configureBrowserStackDeviceOrientation(capabilities);

        configureBrowserStackProject(capabilities);

        configureBrowserStackBuild(capabilities);

        configureBrowserStackName(capabilities);

        configureBrowserStackLocal(capabilities);

        configureBrowserStackDebug(capabilities);

        configureBrowserStackResolution(capabilities);

        configureBrowserStackSeleniumVersion(capabilities);

        configureBrowserStackIeNoFlash(capabilities);

        configureBrowserStackIeDriver(capabilities);

        configureBrowserStackIeEnablePopups(capabilities);

        return capabilities;
    }


    private void configureBrowserStackOs(DesiredCapabilities capabilities) {
        String os = ThucydidesSystemProperty.BROWSERSTACK_OS.from(environmentVariables);
        if (isNotEmpty(os)) {
            capabilities.setCapability("os", os);
        }
    }

    private void configureBrowserStackOsVersion(DesiredCapabilities capabilities) {
        String os_version = ThucydidesSystemProperty.BROWSERSTACK_OS_VERSION.from(environmentVariables);
        if (isNotEmpty(os_version)) {
            capabilities.setCapability("os_version", os_version);
        }
    }

    private void configureBrowserStackBrowser(DesiredCapabilities capabilities){
        String browser = ThucydidesSystemProperty.BROWSERSTACK_BROWSER.from(environmentVariables);
        if (isNotEmpty(browser)) {
            capabilities.setCapability("browser", browser);
        }
    }

    private void configureBrowserStackBrowserVersion(DesiredCapabilities capabilities){
        String browserVersion = ThucydidesSystemProperty.BROWSERSTACK_BROWSER_VERSION.from(environmentVariables);
        if (isNotEmpty(browserVersion)) {
            capabilities.setCapability("browser_version", browserVersion);
        }
    }

    private void configureBrowserStackDevice(DesiredCapabilities capabilities){
        String device = ThucydidesSystemProperty.BROWSERSTACK_DEVICE.from(environmentVariables);
        if (isNotEmpty(device)) {
            capabilities.setCapability("device", device);
        }
    }

    private void configureBrowserStackDeviceOrientation(DesiredCapabilities capabilities){
        String deviceOrientation = ThucydidesSystemProperty.BROWSERSTACK_DEVICE_ORIENTATION.from(environmentVariables);
        if (isNotEmpty(deviceOrientation)) {
            capabilities.setCapability("deviceOrientation", deviceOrientation);
        }
    }

    private void configureBrowserStackProject(DesiredCapabilities capabilities){
        String project = ThucydidesSystemProperty.BROWSERSTACK_PROJECT.from(environmentVariables);
        if (isNotEmpty(project)) {
            capabilities.setCapability("project", project);
        }
    }

    private void configureBrowserStackBuild(DesiredCapabilities capabilities){
        String build = ThucydidesSystemProperty.BROWSERSTACK_BUILD.from(environmentVariables);
        if (isNotEmpty(build)) {
            capabilities.setCapability("build", build);
        }
    }

    private void configureBrowserStackName(DesiredCapabilities capabilities){
        String name = ThucydidesSystemProperty.BROWSERSTACK_SESSION_NAME.from(environmentVariables);
        if (isNotEmpty(name)) {
            capabilities.setCapability("name", name);
        }
    }

    private void configureBrowserStackLocal(DesiredCapabilities capabilities){
        String local = ThucydidesSystemProperty.BROWSERSTACK_LOCAL.from(environmentVariables);
        if (isNotEmpty(local)) {
            capabilities.setCapability("browserstack.local", local);
        }
    }

    private void configureBrowserStackDebug(DesiredCapabilities capabilities){
        String debug = ThucydidesSystemProperty.BROWSERSTACK_DEBUG.from(environmentVariables);
        if (isNotEmpty(debug)) {
            capabilities.setCapability("browserstack.debug", debug);
        }
    }

    private void configureBrowserStackResolution(DesiredCapabilities capabilities){
        String resolution = ThucydidesSystemProperty.BROWSERSTACK_RESOLUTION.from(environmentVariables);
        if (isNotEmpty(resolution)) {
            capabilities.setCapability("browserstack.resolution", resolution);
        }
    }

    private void configureBrowserStackSeleniumVersion(DesiredCapabilities capabilities){
        String seleniumVersion = ThucydidesSystemProperty.BROWSERSTACK_SELENIUM_VERSION.from(environmentVariables);
        if (isNotEmpty(seleniumVersion)) {
            capabilities.setCapability("browserstack.selenium_version", seleniumVersion);
        }
    }

    private void configureBrowserStackIeNoFlash(DesiredCapabilities capabilities){
        String ieNoFlash = ThucydidesSystemProperty.BROWSERSTACK_IE_NO_FLASH.from(environmentVariables);
        if (isNotEmpty(ieNoFlash)) {
            capabilities.setCapability("browserstack.ie.noFlash", ieNoFlash);
        }
    }

    private void configureBrowserStackIeDriver(DesiredCapabilities capabilities){
        String ieDriver = ThucydidesSystemProperty.BROWSERSTACK_IE_DRIVER.from(environmentVariables);
        if (isNotEmpty(ieDriver)) {
            capabilities.setCapability("browserstack.ie.driver", ieDriver);
        }
    }

    private void configureBrowserStackIeEnablePopups(DesiredCapabilities capabilities){
        String ieEnablePopups = ThucydidesSystemProperty.BROWSERSTACK_IE_ENABLE_POPUPS.from(environmentVariables);
        if (isNotEmpty(ieEnablePopups)) {
            capabilities.setCapability("browserstack.ie.enablePopups", ieEnablePopups);
        }
    }

}
