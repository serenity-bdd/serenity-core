package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Get the outer HTML of an element (including the element itself).
 *
 * <p>Sample usage:</p>
 * <pre>
 *     String html = actor.asksFor(OuterHTML.of("#container"));
 *     String html = actor.asksFor(OuterHTML.of(CONTAINER));
 * </pre>
 */
public class OuterHTML implements Question<String> {

    private final Target target;

    private OuterHTML(Target target) {
        this.target = target;
    }

    /**
     * Get outer HTML for an element identified by a selector.
     */
    public static OuterHTML of(String selector) {
        return new OuterHTML(Target.the(selector).locatedBy(selector));
    }

    /**
     * Get outer HTML for a Target element.
     */
    public static OuterHTML of(Target target) {
        return new OuterHTML(target);
    }

    @Override
    public String answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        return (String) locator.evaluate("el => el.outerHTML");
    }

    @Override
    public String toString() {
        return "outer HTML of " + target;
    }
}
