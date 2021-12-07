package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.thucydides.core.annotations.Step;

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
    private Page.WaitForSelectorOptions options;
    Consumer<ElementHandle> nextAction;

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

    public WaitFor withOptions(Page.WaitForSelectorOptions options) {
        this.options = options;
        return this;
    }

    @Override
    @Step("{0} waits for #target")
    public <T extends Actor> void performAs(T actor) {
        ElementHandle resolvedElement = BrowseTheWebWithPlaywright.as(actor).getCurrentPage().waitForSelector(target.asSelector(), options);
        if (nextAction != null) {
            nextAction.accept(resolvedElement);
        }
    }

    public Performable andThen(Consumer<ElementHandle> nextAction) {
        this.nextAction = nextAction;
        return this;
    }
}
