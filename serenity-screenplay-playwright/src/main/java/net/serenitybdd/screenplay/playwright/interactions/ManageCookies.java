package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.options.Cookie;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.List;

/**
 * Manage browser cookies - add, remove, or clear cookies.
 */
public class ManageCookies {

    /**
     * Add a cookie with the given name and value.
     * The cookie will be set for the current page's domain.
     */
    public static AddCookieBuilder addCookie(String name, String value) {
        return new AddCookieBuilder(name, value);
    }

    /**
     * Add multiple cookies at once.
     */
    public static Performable addCookies(List<Cookie> cookies) {
        return new AddMultipleCookies(cookies);
    }

    /**
     * Clear all cookies from the browser context.
     */
    public static Performable clearAll() {
        return new ClearAllCookies();
    }
}

/**
 * Adds multiple cookies at once.
 */
class AddMultipleCookies implements Performable {
    private final List<Cookie> cookies;

    AddMultipleCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    @Override
    @Step("{0} adds multiple cookies")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        ability.addCookies(cookies);
    }
}

/**
 * Clears all cookies from the browser context.
 */
class ClearAllCookies implements Performable {
    @Override
    @Step("{0} clears all cookies")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        ability.clearCookies();
    }
}
