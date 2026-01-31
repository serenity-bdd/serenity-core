package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Scroll an element into view.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     actor.attemptsTo(
 *         ScrollIntoView.element("#footer"),
 *         ScrollIntoView.element(FOOTER_ELEMENT)
 *     );
 * </pre>
 */
public class ScrollIntoView implements Performable {

    private final Target target;
    private Locator.ScrollIntoViewIfNeededOptions options;

    public ScrollIntoView(Target target) {
        this.target = target;
    }

    /**
     * Scroll an element identified by a selector into view.
     */
    public static ScrollIntoView element(String selector) {
        return new ScrollIntoView(Target.the(selector).locatedBy(selector));
    }

    /**
     * Scroll a Target element into view.
     */
    public static ScrollIntoView element(Target target) {
        return new ScrollIntoView(target);
    }

    /**
     * Provide additional scroll options.
     */
    public ScrollIntoView withOptions(Locator.ScrollIntoViewIfNeededOptions options) {
        this.options = options;
        return this;
    }

    @Override
    @Step("{0} scrolls #target into view")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        target.resolveFor(page).scrollIntoViewIfNeeded(options);
        BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
    }
}
