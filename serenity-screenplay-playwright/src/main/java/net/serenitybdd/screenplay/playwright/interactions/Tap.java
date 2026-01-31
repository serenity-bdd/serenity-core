package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Perform a tap (touch) action on an element.
 * This is useful for mobile testing or touch-enabled applications.
 */
public class Tap implements Performable {

    private final String selector;

    private Tap(String selector) {
        this.selector = selector;
    }

    /**
     * Tap on the specified element.
     */
    public static Tap on(String selector) {
        return new Tap(selector);
    }

    @Override
    @Step("{0} taps on #selector")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        Page page = ability.getCurrentPage();
        page.locator(selector).tap();
        ability.notifyScreenChange();
    }
}
