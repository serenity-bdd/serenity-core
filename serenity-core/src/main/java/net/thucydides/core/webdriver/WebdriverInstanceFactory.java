package net.thucydides.core.webdriver;

import com.google.common.base.Splitter;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.exceptions.SerenityManagedException;
import net.thucydides.core.guice.Injectors;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.safari.SafariDriver;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Centralize instantiation of WebDriver drivers.
 */
public class WebdriverInstanceFactory {

    private DriverCapabilityRecord driverProperties;

    public WebdriverInstanceFactory() {
        this.driverProperties = Injectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    public WebDriver newInstanceOf(final Class<? extends WebDriver> webdriverClass) throws IllegalAccessException, InstantiationException {
        return webdriverClass.newInstance();
    }

    public WebDriver newRemoteDriver(URL remoteUrl, Capabilities capabilities) {
        try {
            ensureHostIsAvailableAt(remoteUrl);
            RemoteWebDriver driver = new RemoteWebDriver(remoteUrl, capabilities);
            driverProperties.registerCapabilities("remote", driver.getCapabilities());
            return driver;
        } catch (UnreachableBrowserException unreachableBrowser) {
            String errorMessage = unreachableBrowserErrorMessage(unreachableBrowser);
            throw new SerenityManagedException(errorMessage, unreachableBrowser);
        } catch (UnknownHostException unknownHost) {
            throw new SerenityManagedException(unknownHost.getMessage(), unknownHost);
        }
    }

    private void ensureHostIsAvailableAt(URL remoteUrl) throws UnknownHostException {
        InetAddress.getByName(remoteUrl.getHost());
    }

    private String unreachableBrowserErrorMessage(Exception unreachableBrowser) {
        List<String> errorLines =  Splitter.onPattern("\n").splitToList(unreachableBrowser.getLocalizedMessage());
        Throwable cause = unreachableBrowser.getCause();
        String errorCause = ((cause == null) ? "" :
                              System.lineSeparator() + cause.getClass().getSimpleName() + " - " + cause.getLocalizedMessage());
        return errorLines.get(0) + errorCause;
    }

    public WebDriver newFirefoxDriver(Capabilities capabilities) {
        FirefoxDriver driver = new FirefoxDriver(capabilities);
        driverProperties.registerCapabilities("firefox", driver.getCapabilities());
        return driver;
    }

    public WebDriver newChromeDriver(Capabilities capabilities) {
        ChromeDriver driver = new ChromeDriver(capabilities);
        driverProperties.registerCapabilities("chrome", driver.getCapabilities());
        return driver;
    }

    public WebDriver newAppiumDriver(URL hub, Capabilities capabilities, MobilePlatform platform) {
        switch (platform) {
            case ANDROID:
                AndroidDriver androidDriver = new AndroidDriver(hub, capabilities);
                driverProperties.registerCapabilities("appium", androidDriver.getCapabilities());
                return androidDriver;
            case IOS:
                IOSDriver iosDriver = new IOSDriver(hub, capabilities);
                driverProperties.registerCapabilities("appium", iosDriver.getCapabilities());
                return iosDriver;
        }
        throw new UnsupportedDriverException(platform.name());
    }

    public WebDriver newSafariDriver(Capabilities capabilities) {
        SafariDriver driver = new SafariDriver(capabilities);
        driverProperties.registerCapabilities("chrome", driver.getCapabilities());
        return driver;
    }

    public WebDriver newInternetExplorerDriver(Capabilities capabilities) {
        InternetExplorerDriver driver = new InternetExplorerDriver(capabilities);
        driverProperties.registerCapabilities("iexplorer", driver.getCapabilities());
        return driver;
    }

    public WebDriver newEdgeDriver(Capabilities capabilities) {
        EdgeDriver driver = new EdgeDriver(capabilities);
        driverProperties.registerCapabilities("edge", driver.getCapabilities());
        return driver;
    }

    public WebDriver newHtmlUnitDriver(Capabilities capabilities) {
        HtmlUnitDriver driver = new HtmlUnitDriver(capabilities);
        driverProperties.registerCapabilities("htmlunit", driver.getCapabilities());
        return driver;
    }

    public WebDriver newPhantomDriver(Capabilities capabilities) {
        PhantomJSDriver driver = new PhantomJSDriver(capabilities);
        driverProperties.registerCapabilities("phantomjs", driver.getCapabilities());
        return driver;
    }

}