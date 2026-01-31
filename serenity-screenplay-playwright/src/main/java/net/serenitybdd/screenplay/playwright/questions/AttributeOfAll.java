package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.ArrayList;
import java.util.List;

/**
 * Get an attribute value from all matching elements.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     List&lt;String&gt; hrefs = actor.asksFor(AttributeOfAll.of("a").named("href"));
 *     List&lt;String&gt; ids = actor.asksFor(AttributeOfAll.of(LINKS).named("id"));
 * </pre>
 */
public class AttributeOfAll implements Question<List<String>> {

    private final Target target;
    private String attributeName;

    private AttributeOfAll(Target target) {
        this.target = target;
    }

    /**
     * Get attribute values from elements matching a selector.
     */
    public static AttributeOfAll of(String selector) {
        return new AttributeOfAll(Target.the(selector).locatedBy(selector));
    }

    /**
     * Get attribute values from elements matching a Target.
     */
    public static AttributeOfAll of(Target target) {
        return new AttributeOfAll(target);
    }

    /**
     * Specify the attribute name to retrieve.
     */
    public AttributeOfAll named(String attributeName) {
        this.attributeName = attributeName;
        return this;
    }

    @Override
    public List<String> answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        List<String> values = new ArrayList<>();
        int count = locator.count();
        for (int i = 0; i < count; i++) {
            values.add(locator.nth(i).getAttribute(attributeName));
        }
        return values;
    }

    @Override
    public String toString() {
        return "attribute '" + attributeName + "' of all " + target;
    }
}
