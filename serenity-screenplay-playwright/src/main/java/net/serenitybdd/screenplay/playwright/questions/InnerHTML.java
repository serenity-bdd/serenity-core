package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Get the inner HTML of an element.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     String html = actor.asksFor(InnerHTML.of("#container"));
 *     String html = actor.asksFor(InnerHTML.of(CONTAINER));
 * </pre>
 */
public class InnerHTML implements Question<String> {

    private final Target target;

    private InnerHTML(Target target) {
        this.target = target;
    }

    /**
     * Get inner HTML for an element identified by a selector.
     */
    public static InnerHTML of(String selector) {
        return new InnerHTML(Target.the(selector).locatedBy(selector));
    }

    /**
     * Get inner HTML for a Target element.
     */
    public static InnerHTML of(Target target) {
        return new InnerHTML(target);
    }

    @Override
    public String answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        return locator.innerHTML();
    }

    @Override
    public String toString() {
        return "inner HTML of " + target;
    }
}
