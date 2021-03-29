package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Open a URL on a given URL.
 */
public class Open {

    public static Performable url(String url) {
        return Task.where("{0} opens the URL " + url,
                actor -> BrowseTheWebWithPlaywright.as(actor).getCurrentPage().navigate(url)
        );
    }
}
