package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Get the count of matching elements.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     Integer count = actor.asksFor(Count.of(".item"));
 *     Integer count = actor.asksFor(Count.of(LIST_ITEMS));
 * </pre>
 */
public class Count implements Question<Integer> {

    private final Target target;

    private Count(Target target) {
        this.target = target;
    }

    /**
     * Count elements matching a selector.
     */
    public static Count of(String selector) {
        return new Count(Target.the(selector).locatedBy(selector));
    }

    /**
     * Count elements matching a Target.
     */
    public static Count of(Target target) {
        return new Count(target);
    }

    @Override
    public Integer answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        return locator.count();
    }

    @Override
    public String toString() {
        return "count of " + target;
    }
}
