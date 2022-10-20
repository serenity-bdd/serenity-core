package net.thucydides.core.webdriver;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.Logs;

import java.util.Set;

public class OptionsFacade implements WebDriver.Options {

    private final WebDriver.Options options;
    private final WebDriverFacade webDriverFacade;

    public OptionsFacade(WebDriver.Options options, WebDriverFacade webDriverFacade) {
        this.options = options;
        this.webDriverFacade = webDriverFacade;
    }

    @Override
    public void addCookie(Cookie cookie) {
        options.addCookie(cookie);
    }

    @Override
    public void deleteCookieNamed(String s) {
        options.deleteCookieNamed(s);
    }

    @Override
    public void deleteCookie(Cookie cookie) {
        options.deleteCookie(cookie);
    }

    @Override
    public void deleteAllCookies() {
        options.deleteAllCookies();
    }

    @Override
    public Set<Cookie> getCookies() {
        return options.getCookies();
    }

    @Override
    public Cookie getCookieNamed(String s) {
        return options.getCookieNamed(s);
    }

    @Override
    public WebDriver.Timeouts timeouts() {
        return new TimeoutsFacade(webDriverFacade, (options != null) ? options.timeouts() : null);
    }

    @Override
    public WebDriver.Window window() {
        return options.window();
    }

    @Override
    public Logs logs() {
        return options.logs();
    }
}
