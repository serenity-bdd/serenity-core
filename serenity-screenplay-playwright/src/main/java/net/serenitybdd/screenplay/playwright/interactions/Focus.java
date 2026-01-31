package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Focus on an element or blur (unfocus) the currently focused element.
 */
public class Focus {

    /**
     * Focus on the specified element.
     */
    public static Performable on(String selector) {
        return new FocusOnElement(selector);
    }

    /**
     * Remove focus from the currently focused element.
     */
    public static Performable blur() {
        return new BlurActiveElement();
    }

    /**
     * Remove focus from a specific element.
     */
    public static Performable blurElement(String selector) {
        return new BlurSpecificElement(selector);
    }
}

class FocusOnElement implements Performable {
    private final String selector;

    FocusOnElement(String selector) {
        this.selector = selector;
    }

    @Override
    @Step("{0} focuses on #selector")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        Page page = ability.getCurrentPage();
        page.locator(selector).focus();
        ability.notifyScreenChange();
    }
}

class BlurActiveElement implements Performable {
    @Override
    @Step("{0} removes focus from the active element")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        Page page = ability.getCurrentPage();
        page.evaluate("document.activeElement?.blur()");
        ability.notifyScreenChange();
    }
}

class BlurSpecificElement implements Performable {
    private final String selector;

    BlurSpecificElement(String selector) {
        this.selector = selector;
    }

    @Override
    @Step("{0} removes focus from #selector")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        Page page = ability.getCurrentPage();
        page.locator(selector).blur();
        ability.notifyScreenChange();
    }
}
