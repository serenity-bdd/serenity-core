package net.thucydides.core.webdriver;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.FileSystemUtils;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.openqa.selenium.WebDriver;

import java.io.File;

public class StaticTestSite {

    private WebDriverFactory factory;
    private ThucydidesWebdriverManager webdriverManager;
    private EnvironmentVariables environmentVariables;

    public StaticTestSite() {
        factory = new WebDriverFactory();
        environmentVariables = MockEnvironmentVariables.fromSystemEnvironment();
        webdriverManager = new ThucydidesWebdriverManager(factory, new SystemPropertiesConfiguration(environmentVariables));
    }

    public WebDriver open(String driverType) {
        environmentVariables.setProperty("chrome.switches","--homepage=about:blank,--no-first-run");
        WebDriver driver;
        if (driverType != null) {
            driver = webdriverManager.getWebdriver(driverType);
        } else {
            driver = webdriverManager.getWebdriver();
        }
        if (factory.usesSauceLabs()) {
            driver.get("http://wakaleo.com/thucydides-tests/index.html");
        } else {
            File testSite = fileInClasspathCalled("static-site/index.html");
            driver.get("file://" + testSite.getAbsolutePath());
        }
        return driver;
    }

    public void close() {
        webdriverManager.closeAllCurrentDrivers();
    }

    public WebDriver open(String remoteUrl, String correspondingLocalFile, String drivername) {
        WebDriver driver = webdriverManager.getWebdriver(drivername);
        if (factory.usesSauceLabs()) {
            driver.get(remoteUrl);
        } else {
            File testSite = fileInClasspathCalled(correspondingLocalFile);
            driver.get("file://" + testSite.getAbsolutePath());
        }
        return driver;
    }

    public WebDriver open(String remoteUrl, String correspondingLocalFile) {
        WebDriver driver = webdriverManager.getWebdriver();
        if (factory.usesSauceLabs()) {
            driver.get(remoteUrl);
        } else {
            File testSite = fileInClasspathCalled(correspondingLocalFile);
            driver.get("file://" + testSite.getAbsolutePath());
        }
        return driver;
    }

    public static File fileInClasspathCalled(final String resourceName) {
        return FileSystemUtils.getResourceAsFile(resourceName);
    }
}
