package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.List;

/**
 * Get the text content of all matching elements.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     List&lt;String&gt; texts = actor.asksFor(TextOfAll.of(".list-item"));
 *     List&lt;String&gt; texts = actor.asksFor(TextOfAll.of(LIST_ITEMS));
 * </pre>
 */
public class TextOfAll implements Question<List<String>> {

    private final Target target;

    private TextOfAll(Target target) {
        this.target = target;
    }

    /**
     * Get text of all elements matching a selector.
     */
    public static TextOfAll of(String selector) {
        return new TextOfAll(Target.the(selector).locatedBy(selector));
    }

    /**
     * Get text of all elements matching a Target.
     */
    public static TextOfAll of(Target target) {
        return new TextOfAll(target);
    }

    @Override
    public List<String> answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        return locator.allTextContents();
    }

    @Override
    public String toString() {
        return "text of all " + target;
    }
}
