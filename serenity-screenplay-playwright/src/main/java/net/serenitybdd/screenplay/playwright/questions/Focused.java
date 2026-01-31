package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Check if an element has focus.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     Boolean focused = actor.asksFor(Focused.of("#email"));
 *     Boolean focused = actor.asksFor(Focused.of(EMAIL_FIELD));
 * </pre>
 */
public class Focused implements Question<Boolean> {

    private final Target target;

    private Focused(Target target) {
        this.target = target;
    }

    /**
     * Check if an element identified by a selector has focus.
     */
    public static Focused of(String selector) {
        return new Focused(Target.the(selector).locatedBy(selector));
    }

    /**
     * Check if a Target element has focus.
     */
    public static Focused of(Target target) {
        return new Focused(target);
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        // Playwright uses evaluate to check focus
        return (Boolean) locator.evaluate("el => el === document.activeElement");
    }

    @Override
    public String toString() {
        return "focused state of " + target;
    }
}
