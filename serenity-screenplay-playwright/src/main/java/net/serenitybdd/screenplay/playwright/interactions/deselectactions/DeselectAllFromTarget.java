package net.serenitybdd.screenplay.playwright.interactions.deselectactions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Deselect all options from a multi-select dropdown.
 */
public class DeselectAllFromTarget implements Performable {
    private final Target target;

    public DeselectAllFromTarget(Target target) {
        this.target = target;
    }

    @Step("{0} deselects all options in #target")
    public <T extends Actor> void performAs(T theUser) {
        Page page = BrowseTheWebWithPlaywright.as(theUser).getCurrentPage();
        Locator locator = target.resolveFor(page);

        // Use Playwright's selectOption with empty array to clear all selections
        locator.selectOption(new String[]{});
        BrowseTheWebWithPlaywright.as(theUser).notifyScreenChange();
    }
}
