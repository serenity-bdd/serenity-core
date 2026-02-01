package net.serenitybdd.screenplay.playwright.interactions.deselectactions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Deselect an option from a multi-select dropdown by its visible text.
 */
public class DeselectByVisibleTextFromTarget implements Interaction {
    private final Target target;
    private final String option;

    public DeselectByVisibleTextFromTarget(Target target, String option) {
        this.target = target;
        this.option = option;
    }

    @Step("{0} deselects '{1}' from #target")
    public <T extends Actor> void performAs(T theUser) {
        Page page = BrowseTheWebWithPlaywright.as(theUser).getCurrentPage();
        Locator locator = target.resolveFor(page);

        // Use JavaScript to deselect option by visible text
        String escapedOption = option.replace("\"", "\\\"");
        String script = "el => {" +
                "const textToDeselect = \"" + escapedOption + "\";" +
                "Array.from(el.options).forEach(opt => {" +
                "  if (opt.text === textToDeselect || opt.textContent.trim() === textToDeselect) opt.selected = false;" +
                "});" +
                "el.dispatchEvent(new Event('change', { bubbles: true }));" +
                "}";
        locator.evaluate(script);
        BrowseTheWebWithPlaywright.as(theUser).notifyScreenChange();
    }
}
