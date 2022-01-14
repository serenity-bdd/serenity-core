package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.thucydides.core.annotations.Step;

/**
 * This method clicks an element matching selector by performing the following steps:
 * Find an element match matching selector. If there is none, wait until a matching element is attached to the DOM.
 * Wait for actionability  checks on the matched element, unless force option is set. If the element is detached during the checks, the whole action is retried.
 * Scroll the element into view if needed.
 * Use Page.mouse() to click in the center of the element, or the specified position.
 * Wait for initiated navigations to either succeed or fail, unless noWaitAfter option is set.
 * When all steps combined have not finished during the specified timeout, this method rejects with a TimeoutError. Passing zero timeout disables this.
 * <p>
 * Sample usage:
 * <pre>
 *     Click.on("#searchbutton");
 * </pre>
 */
public class Click implements Performable {

    /**
     * Default constructor required by Screenplay
     */
    public Click() {
    }

    private Target target;
    private Page.ClickOptions options;

    public Click(Target target) {
        this.target = target;
    }

    public static Click on(String selector) {
        return new Click(Target.the(selector).locatedBy(selector));
    }

    public static Click on(Target target) {
        return new Click(target);
    }

    public Performable withOptions(Page.ClickOptions options) {
        this.options = options;
        return this;
    }

    @Override
    @Step("{0} clicks on #target")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright.as(actor).getCurrentPage().click(target.asSelector(), options);
        BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
    }
}
