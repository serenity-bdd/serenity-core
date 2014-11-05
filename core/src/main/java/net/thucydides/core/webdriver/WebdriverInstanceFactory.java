package net.thucydides.core.webdriver;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.net.URL;

/**
 * Centralize instantiation of WebDriver drivers.
 */
public class WebdriverInstanceFactory {

    public WebdriverInstanceFactory() {
    }

    public WebDriver newInstanceOf(final Class<? extends WebDriver> webdriverClass) throws IllegalAccessException, InstantiationException {
        return webdriverClass.newInstance();
    }

    public WebDriver newRemoteDriver(URL remoteUrl, Capabilities capabilities) {
        return new RemoteWebDriver(remoteUrl, capabilities);
    }

    public WebDriver newFirefoxDriver(Capabilities capabilities) {
        return new FirefoxDriver(capabilities);
    }

    public WebDriver newChromeDriver(Capabilities capabilities) {
        return new ChromeDriver(capabilities);
    }

    public WebDriver newSafariDriver(Capabilities capabilities) {
        return new SafariDriver(capabilities);
    }

    public WebDriver newInternetExplorerDriver(Capabilities capabilities) {
        return new InternetExplorerDriver(capabilities);
    }

    public WebDriver newHtmlUnitDriver(Capabilities capabilities) {
        return new HtmlUnitDriver(capabilities);
    }

    public WebDriver newPhantomDriver(Capabilities caps) {
        return new PhantomJSDriver(caps);
    }
}