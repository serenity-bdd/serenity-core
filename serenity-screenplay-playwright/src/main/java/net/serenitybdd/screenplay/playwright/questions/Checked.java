package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Check if a checkbox or radio button is checked.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     Boolean checked = actor.asksFor(Checked.of("#agree-checkbox"));
 *     Boolean checked = actor.asksFor(Checked.of(TERMS_CHECKBOX));
 * </pre>
 */
public class Checked implements Question<Boolean> {

    private final Target target;

    private Checked(Target target) {
        this.target = target;
    }

    /**
     * Check if an element identified by a selector is checked.
     */
    public static Checked of(String selector) {
        return new Checked(Target.the(selector).locatedBy(selector));
    }

    /**
     * Check if a Target element is checked.
     */
    public static Checked of(Target target) {
        return new Checked(target);
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        return locator.isChecked();
    }

    @Override
    public String toString() {
        return "checked state of " + target;
    }
}
