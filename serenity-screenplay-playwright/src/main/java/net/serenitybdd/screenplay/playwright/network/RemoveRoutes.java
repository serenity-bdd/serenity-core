package net.serenitybdd.screenplay.playwright.network;

import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Remove previously registered network route handlers.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // Remove all route handlers
 *     actor.attemptsTo(RemoveRoutes.all());
 *
 *     // Remove routes matching a specific pattern
 *     actor.attemptsTo(RemoveRoutes.forUrl("**\/api/**"));
 * </pre>
 */
public class RemoveRoutes {

    private RemoveRoutes() {
        // Factory class - prevent instantiation
    }

    /**
     * Remove all registered route handlers.
     */
    public static Performable all() {
        return new RemoveAllRoutes();
    }

    /**
     * Remove route handlers matching the specified URL pattern.
     */
    public static Performable forUrl(String urlPattern) {
        return new RemoveRoutesForUrl(urlPattern);
    }
}

class RemoveAllRoutes implements Performable {
    @Override
    @Step("{0} removes all network route handlers")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.unrouteAll();
    }
}

class RemoveRoutesForUrl implements Performable {
    private final String urlPattern;

    RemoveRoutesForUrl(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    @Override
    @Step("{0} removes route handlers for #urlPattern")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.unroute(urlPattern);
    }
}
