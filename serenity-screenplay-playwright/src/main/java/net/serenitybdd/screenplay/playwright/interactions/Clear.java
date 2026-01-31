package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Clear an input field.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     actor.attemptsTo(
 *         Clear.field("#email"),
 *         Clear.field(EMAIL_FIELD)
 *     );
 * </pre>
 */
public class Clear implements Performable {

    private final Target target;

    public Clear(Target target) {
        this.target = target;
    }

    /**
     * Clear an input field identified by a selector.
     */
    public static Clear field(String selector) {
        return new Clear(Target.the(selector).locatedBy(selector));
    }

    /**
     * Clear a Target input field.
     */
    public static Clear field(Target target) {
        return new Clear(target);
    }

    @Override
    @Step("{0} clears #target")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        target.resolveFor(page).clear();
        BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
    }
}
