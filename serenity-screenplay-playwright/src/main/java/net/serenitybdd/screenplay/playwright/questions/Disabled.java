package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Check if an element is disabled.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     Boolean disabled = actor.asksFor(Disabled.of("#button"));
 *     Boolean disabled = actor.asksFor(Disabled.of(SUBMIT_BUTTON));
 * </pre>
 */
public class Disabled implements Question<Boolean> {

    private final Target target;

    private Disabled(Target target) {
        this.target = target;
    }

    /**
     * Check if an element identified by a selector is disabled.
     */
    public static Disabled of(String selector) {
        return new Disabled(Target.the(selector).locatedBy(selector));
    }

    /**
     * Check if a Target element is disabled.
     */
    public static Disabled of(Target target) {
        return new Disabled(target);
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        return locator.isDisabled();
    }

    @Override
    public String toString() {
        return "disabled state of " + target;
    }
}
