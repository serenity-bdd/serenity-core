package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Check if an element is present in the DOM.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     Boolean present = actor.asksFor(Presence.of("#element"));
 *     Boolean present = actor.asksFor(Presence.of(MY_TARGET));
 * </pre>
 */
public class Presence implements Question<Boolean> {

    private final Target target;

    private Presence(Target target) {
        this.target = target;
    }

    /**
     * Check if an element identified by a selector is present.
     */
    public static Presence of(String selector) {
        return new Presence(Target.the(selector).locatedBy(selector));
    }

    /**
     * Check if a Target element is present.
     */
    public static Presence of(Target target) {
        return new Presence(target);
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        return locator.count() > 0;
    }

    @Override
    public String toString() {
        return "presence of " + target;
    }
}
