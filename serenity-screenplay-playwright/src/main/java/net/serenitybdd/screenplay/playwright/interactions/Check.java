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
 *     Check.the("#searchbutton");
 * </pre>
 */
public class Check implements Performable {

    /**
     * Default constructor required by Screenplay
     */
    public Check() {
    }

    private Target target;
    private Page.CheckOptions options;

    public Check(Target target) {
        this.target = target;
    }

    public static Check the(String selector) {
        return new Check(Target.the(selector).locatedBy(selector));
    }

    public static Check the(Target target) {
        return new Check(target);
    }

    public Performable withOptions(Page.CheckOptions options) {
        this.options = options;
        return this;
    }

    @Override
    @Step("{0} checks #target")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright.as(actor).getCurrentPage().check(target.asSelector(), options);
        BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
    }
}
