package net.thucydides.core.webdriver.stubs;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.Logs;

import java.util.Collections;
import java.util.Set;

/**
 * A description goes here.
 * User: johnsmart
 * Date: 6/02/12
 * Time: 10:37 AM
 */
public class OptionsStub implements WebDriver.Options {

    public OptionsStub() {
    }

    public void addCookie(Cookie cookie) {
    }

    public void deleteCookieNamed(String name) {
    }

    public void deleteCookie(Cookie cookie) {
    }

    public void deleteAllCookies() {
    }

    public Set<Cookie> getCookies() {
        return Collections.EMPTY_SET;
    }

    public Cookie getCookieNamed(String name) {
        return new CookieStub(name,"");
    }

    public WebDriver.Timeouts timeouts() {
        return new TimeoutsStub();
    }

    public WebDriver.Window window() {
        return new WindowStub();
    }

    public Logs logs() {
        return new Logs(){
            public LogEntries get(String s) {
                return null;
            }

            public Set<String> getAvailableLogTypes() {
                return null;
            }
        };
    }
}
