package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Check if an element is editable.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     Boolean editable = actor.asksFor(Editable.of("#email"));
 *     Boolean editable = actor.asksFor(Editable.of(EMAIL_FIELD));
 * </pre>
 */
public class Editable implements Question<Boolean> {

    private final Target target;

    private Editable(Target target) {
        this.target = target;
    }

    /**
     * Check if an element identified by a selector is editable.
     */
    public static Editable of(String selector) {
        return new Editable(Target.the(selector).locatedBy(selector));
    }

    /**
     * Check if a Target element is editable.
     */
    public static Editable of(Target target) {
        return new Editable(target);
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        return locator.isEditable();
    }

    @Override
    public String toString() {
        return "editable state of " + target;
    }
}
