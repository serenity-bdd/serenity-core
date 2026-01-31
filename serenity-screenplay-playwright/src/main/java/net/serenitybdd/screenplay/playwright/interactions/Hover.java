package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Hover over an element.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     actor.attemptsTo(
 *         Hover.over("#menu-item"),
 *         Hover.over(DROPDOWN_TRIGGER)
 *     );
 * </pre>
 */
public class Hover implements Performable {

    private final Target target;
    private Locator.HoverOptions options;

    public Hover(Target target) {
        this.target = target;
    }

    /**
     * Hover over an element identified by a selector.
     */
    public static Hover over(String selector) {
        return new Hover(Target.the(selector).locatedBy(selector));
    }

    /**
     * Hover over a Target element.
     */
    public static Hover over(Target target) {
        return new Hover(target);
    }

    /**
     * Provide additional hover options.
     */
    public Hover withOptions(Locator.HoverOptions options) {
        this.options = options;
        return this;
    }

    @Override
    @Step("{0} hovers over #target")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        target.resolveFor(page).hover(options);
        BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
    }
}
