package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Browser navigation interactions - back, forward, refresh.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     actor.attemptsTo(
 *         Navigate.back(),
 *         Navigate.forward(),
 *         Navigate.refresh()
 *     );
 * </pre>
 */
public class Navigate {

    /**
     * Navigate back in browser history.
     */
    public static Performable back() {
        return new NavigateBack();
    }

    /**
     * Navigate forward in browser history.
     */
    public static Performable forward() {
        return new NavigateForward();
    }

    /**
     * Refresh the current page.
     */
    public static Performable refresh() {
        return new NavigateRefresh();
    }

    /**
     * Navigate to a specific URL.
     */
    public static Performable toUrl(String url) {
        return Open.url(url);
    }
}

/**
 * Navigate back in browser history.
 */
class NavigateBack implements Performable {
    @Override
    @Step("{0} navigates back")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.goBack();
        BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
    }
}

/**
 * Navigate forward in browser history.
 */
class NavigateForward implements Performable {
    @Override
    @Step("{0} navigates forward")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.goForward();
        BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
    }
}

/**
 * Navigate and refresh the current page.
 */
class NavigateRefresh implements Performable {
    @Override
    @Step("{0} refreshes the page")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.reload();
        BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
    }
}
