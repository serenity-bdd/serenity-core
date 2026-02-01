package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Check if an element is absent from the DOM.
 * This is the inverse of {@link Presence}.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     Boolean absent = actor.asksFor(Absence.of("#element"));
 *     Boolean absent = actor.asksFor(Absence.of(MY_TARGET));
 * </pre>
 */
public class Absence implements Question<Boolean> {

    private final Target target;

    private Absence(Target target) {
        this.target = target;
    }

    /**
     * Check if an element identified by a selector is absent.
     */
    public static Absence of(String selector) {
        return new Absence(Target.the(selector).locatedBy(selector));
    }

    /**
     * Check if a Target element is absent.
     */
    public static Absence of(Target target) {
        return new Absence(target);
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        return locator.count() == 0;
    }

    @Override
    public String toString() {
        return "absence of " + target;
    }
}
