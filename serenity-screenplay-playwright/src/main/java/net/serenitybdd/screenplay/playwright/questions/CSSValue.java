package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Get the computed CSS value of an element.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     String color = actor.asksFor(CSSValue.of("#element").named("color"));
 *     String display = actor.asksFor(CSSValue.of(MY_TARGET).named("display"));
 * </pre>
 */
public class CSSValue implements Question<String> {

    private final Target target;
    private String propertyName;

    private CSSValue(Target target) {
        this.target = target;
    }

    /**
     * Get CSS value for an element identified by a selector.
     */
    public static CSSValue of(String selector) {
        return new CSSValue(Target.the(selector).locatedBy(selector));
    }

    /**
     * Get CSS value for a Target element.
     */
    public static CSSValue of(Target target) {
        return new CSSValue(target);
    }

    /**
     * Specify the CSS property name to retrieve.
     */
    public CSSValue named(String propertyName) {
        this.propertyName = propertyName;
        return this;
    }

    @Override
    public String answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        // Use evaluate to get computed style
        return (String) locator.evaluate(
            "(el, prop) => window.getComputedStyle(el).getPropertyValue(prop)",
            propertyName
        );
    }

    @Override
    public String toString() {
        return "CSS value '" + propertyName + "' of " + target;
    }
}
