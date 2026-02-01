package net.serenitybdd.screenplay.playwright.interactions.deselectactions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Deselect an option from a multi-select dropdown by its index (0-based).
 */
public class DeselectByIndexFromTarget implements Interaction {
    private final Target target;
    private final int index;

    public DeselectByIndexFromTarget(Target target, int index) {
        this.target = target;
        this.index = index;
    }

    @Step("{0} deselects index #index in #target")
    public <T extends Actor> void performAs(T theUser) {
        Page page = BrowseTheWebWithPlaywright.as(theUser).getCurrentPage();
        Locator locator = target.resolveFor(page);

        // Use JavaScript to deselect option by index
        String script = "el => {" +
                "const indexToDeselect = " + index + ";" +
                "if (el.options[indexToDeselect]) {" +
                "  el.options[indexToDeselect].selected = false;" +
                "  el.dispatchEvent(new Event('change', { bubbles: true }));" +
                "}" +
                "}";
        locator.evaluate(script);
        BrowseTheWebWithPlaywright.as(theUser).notifyScreenChange();
    }
}
