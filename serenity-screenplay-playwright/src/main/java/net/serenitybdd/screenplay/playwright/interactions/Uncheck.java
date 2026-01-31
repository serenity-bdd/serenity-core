package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Uncheck a checkbox element.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     actor.attemptsTo(
 *         Uncheck.checkbox("#terms"),
 *         Uncheck.checkbox(TERMS_CHECKBOX)
 *     );
 * </pre>
 */
public class Uncheck implements Performable {

    private final Target target;
    private Locator.UncheckOptions options;

    public Uncheck(Target target) {
        this.target = target;
    }

    /**
     * Uncheck a checkbox identified by a selector.
     */
    public static Uncheck checkbox(String selector) {
        return new Uncheck(Target.the(selector).locatedBy(selector));
    }

    /**
     * Uncheck a Target checkbox.
     */
    public static Uncheck checkbox(Target target) {
        return new Uncheck(target);
    }

    /**
     * Provide additional uncheck options.
     */
    public Uncheck withOptions(Locator.UncheckOptions options) {
        this.options = options;
        return this;
    }

    @Override
    @Step("{0} unchecks #target")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        target.resolveFor(page).uncheck(options);
        BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
    }
}
