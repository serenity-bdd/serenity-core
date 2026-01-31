package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Get the text of the currently selected option in a select element.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     String selectedText = actor.asksFor(SelectedOptionText.of("#country"));
 *     String selectedText = actor.asksFor(SelectedOptionText.of(COUNTRY_DROPDOWN));
 * </pre>
 */
public class SelectedOptionText implements Question<String> {

    private final Target target;

    private SelectedOptionText(Target target) {
        this.target = target;
    }

    /**
     * Get selected option text from a select element identified by a selector.
     */
    public static SelectedOptionText of(String selector) {
        return new SelectedOptionText(Target.the(selector).locatedBy(selector));
    }

    /**
     * Get selected option text from a Target select element.
     */
    public static SelectedOptionText of(Target target) {
        return new SelectedOptionText(target);
    }

    @Override
    public String answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        // Get the text of the selected option
        return (String) locator.evaluate("select => select.options[select.selectedIndex].text");
    }

    @Override
    public String toString() {
        return "selected option text of " + target;
    }
}
