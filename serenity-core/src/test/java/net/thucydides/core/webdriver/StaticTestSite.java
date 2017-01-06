package net.thucydides.core.webdriver;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.FileSystemUtils;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import org.openqa.selenium.WebDriver;

import java.io.File;

public class StaticTestSite {

    private WebDriverFactory factory;
   // private SerenityWebdriverManager webdriverManager;
    private EnvironmentVariables environmentVariables;

    public StaticTestSite() {
        this(MockEnvironmentVariables.fromSystemEnvironment());
    }

    public StaticTestSite(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        factory = new WebDriverFactory(environmentVariables);
        //webdriverManager = new SerenityWebdriverManager(factory, new SystemPropertiesConfiguration(environmentVariables));
    }

    private String homepage = "index.html";

    public StaticTestSite withStaticPage() {
        homepage = "static-index.html";
        return this;
    }

    public WebDriver open(String driverType) {
        environmentVariables.setProperty("chrome.switches","--homepage=about:blank,--no-first-run");

        WebDriver driver = ThucydidesWebDriverSupport.getWebdriverManager(factory, new SystemPropertiesConfiguration(environmentVariables)).getWebdriver(driverType);
        if (factory.usesSauceLabs()) {
            driver.get("http://wakaleo.com/thucydides-tests/" + homepage);
        } else {
            File testSite = fileInClasspathCalled("static-site/" + homepage);
            driver.get("file://" + testSite.getAbsolutePath());
        }
        return driver;
    }

    public void close() {
        ThucydidesWebDriverSupport.closeAllDrivers();
    }

    public WebDriver open(String remoteUrl, String correspondingLocalFile, String drivername) {
        WebDriver driver = ThucydidesWebDriverSupport.getWebdriverManager().getWebdriver(drivername);
        if (factory.usesSauceLabs()) {
            driver.get(remoteUrl);
        } else {
            File testSite = fileInClasspathCalled(correspondingLocalFile);
            driver.get("file://" + testSite.getAbsolutePath());
        }
        return driver;
    }

    public WebDriver open(String remoteUrl, String correspondingLocalFile) {
        WebDriver driver = ThucydidesWebDriverSupport.getWebdriverManager().getWebdriver();
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
