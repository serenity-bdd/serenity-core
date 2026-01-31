package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Check if an element is enabled.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     Boolean enabled = actor.asksFor(Enabled.of("#button"));
 *     Boolean enabled = actor.asksFor(Enabled.of(SUBMIT_BUTTON));
 * </pre>
 */
public class Enabled implements Question<Boolean> {

    private final Target target;

    private Enabled(Target target) {
        this.target = target;
    }

    /**
     * Check if an element identified by a selector is enabled.
     */
    public static Enabled of(String selector) {
        return new Enabled(Target.the(selector).locatedBy(selector));
    }

    /**
     * Check if a Target element is enabled.
     */
    public static Enabled of(Target target) {
        return new Enabled(target);
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        return locator.isEnabled();
    }

    @Override
    public String toString() {
        return "enabled state of " + target;
    }
}
