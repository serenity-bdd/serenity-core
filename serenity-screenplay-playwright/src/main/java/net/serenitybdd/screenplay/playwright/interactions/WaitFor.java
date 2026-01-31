package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.function.Consumer;

/**
 * Wait for some element or state.
 * <p>
 * Sample usage:
 * <pre>
 *     WaitFor.selector("#searchbutton");
 * </pre>
 */
public class WaitFor implements Performable {

    /**
     * Default constructor required by Screenplay
     */
    public WaitFor() {
    }

    private Target target;
    private Locator.WaitForOptions options;
    Consumer<Locator> nextAction;

    public WaitFor(Target target) {
        this.target = target;
    }

    public static WaitFor selector(String selector) {
        return new WaitFor(Target.the(selector).locatedBy(selector));
    }

    /**
     * Wait for an element to be in a given state.
     */
    public static WaitFor selector(Target target) {
        return new WaitFor(target);
    }

    public WaitFor withOptions(Locator.WaitForOptions options) {
        this.options = options;
        return this;
    }

    /**
     * Set timeout in milliseconds.
     */
    public WaitFor withTimeout(double timeoutMs) {
        if (this.options == null) {
            this.options = new Locator.WaitForOptions();
        }
        this.options.setTimeout(timeoutMs);
        return this;
    }

    @Override
    @Step("{0} waits for #target")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        locator.waitFor(options);
        if (nextAction != null) {
            nextAction.accept(locator);
        }
    }

    public Performable andThen(Consumer<Locator> nextAction) {
        this.nextAction = nextAction;
        return this;
    }
}
