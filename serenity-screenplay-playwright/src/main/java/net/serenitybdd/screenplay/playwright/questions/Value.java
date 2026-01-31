package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Get the value of an input element.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     String value = actor.asksFor(Value.of("#email"));
 *     String value = actor.asksFor(Value.of(EMAIL_FIELD));
 * </pre>
 */
public class Value implements Question<String> {

    private final Target target;

    private Value(Target target) {
        this.target = target;
    }

    /**
     * Get value for an input element identified by a selector.
     */
    public static Value of(String selector) {
        return new Value(Target.the(selector).locatedBy(selector));
    }

    /**
     * Get value for a Target input element.
     */
    public static Value of(Target target) {
        return new Value(target);
    }

    @Override
    public String answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        return locator.inputValue();
    }

    @Override
    public String toString() {
        return "value of " + target;
    }
}
