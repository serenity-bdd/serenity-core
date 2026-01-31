package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.List;

/**
 * Get the text of all options in a select element.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     List&lt;String&gt; options = actor.asksFor(AllOptionTexts.of("#country"));
 *     List&lt;String&gt; options = actor.asksFor(AllOptionTexts.of(COUNTRY_DROPDOWN));
 * </pre>
 */
public class AllOptionTexts implements Question<List<String>> {

    private final Target target;

    private AllOptionTexts(Target target) {
        this.target = target;
    }

    /**
     * Get all option texts from a select element identified by a selector.
     */
    public static AllOptionTexts of(String selector) {
        return new AllOptionTexts(Target.the(selector).locatedBy(selector));
    }

    /**
     * Get all option texts from a Target select element.
     */
    public static AllOptionTexts of(Target target) {
        return new AllOptionTexts(target);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        // Get all option texts
        return (List<String>) locator.evaluate(
            "select => Array.from(select.options).map(opt => opt.text)"
        );
    }

    @Override
    public String toString() {
        return "all option texts of " + target;
    }
}
