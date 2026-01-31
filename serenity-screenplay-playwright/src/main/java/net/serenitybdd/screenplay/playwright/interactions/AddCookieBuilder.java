package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.options.Cookie;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Builder for creating a cookie with various options.
 */
public class AddCookieBuilder implements Performable {
    private final String name;
    private final String value;
    private String domain;
    private String path = "/";
    private Double expires;
    private Boolean httpOnly;
    private Boolean secure;
    private String sameSite;

    AddCookieBuilder(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Set the domain for the cookie.
     */
    public AddCookieBuilder forDomain(String domain) {
        this.domain = domain;
        return this;
    }

    /**
     * Set the path for the cookie.
     */
    public AddCookieBuilder withPath(String path) {
        this.path = path;
        return this;
    }

    /**
     * Set the expiration time as Unix timestamp in seconds.
     */
    public AddCookieBuilder expiresAt(double timestamp) {
        this.expires = timestamp;
        return this;
    }

    /**
     * Mark the cookie as HTTP-only.
     */
    public AddCookieBuilder httpOnly() {
        this.httpOnly = true;
        return this;
    }

    /**
     * Mark the cookie as secure.
     */
    public AddCookieBuilder secure() {
        this.secure = true;
        return this;
    }

    /**
     * Set the SameSite attribute (Strict, Lax, or None).
     */
    public AddCookieBuilder sameSite(String sameSite) {
        this.sameSite = sameSite;
        return this;
    }

    @Override
    @Step("{0} adds cookie '#name'")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);

        // Get domain from current page if not specified
        String cookieDomain = domain;
        if (cookieDomain == null) {
            String currentUrl = ability.getCurrentPage().url();
            try {
                java.net.URI uri = new java.net.URI(currentUrl);
                cookieDomain = uri.getHost();
            } catch (java.net.URISyntaxException e) {
                throw new IllegalStateException("Cannot determine domain from URL: " + currentUrl, e);
            }
        }

        Cookie cookie = new Cookie(name, value)
            .setDomain(cookieDomain)
            .setPath(path);

        if (expires != null) {
            cookie.setExpires(expires);
        }
        if (httpOnly != null) {
            cookie.setHttpOnly(httpOnly);
        }
        if (secure != null) {
            cookie.setSecure(secure);
        }
        if (sameSite != null) {
            cookie.setSameSite(com.microsoft.playwright.options.SameSiteAttribute.valueOf(sameSite.toUpperCase()));
        }

        ability.addCookie(cookie);
    }
}
