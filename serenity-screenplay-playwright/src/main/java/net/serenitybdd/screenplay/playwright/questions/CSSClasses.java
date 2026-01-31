package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.Arrays;
import java.util.List;

/**
 * Get CSS classes of an element or check if it has a specific class.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     List&lt;String&gt; classes = actor.asksFor(CSSClasses.of("#element"));
 *     Boolean hasClass = actor.asksFor(CSSClasses.of("#element").contains("active"));
 * </pre>
 */
public class CSSClasses implements Question<List<String>> {

    private final Target target;

    private CSSClasses(Target target) {
        this.target = target;
    }

    /**
     * Get CSS classes for an element identified by a selector.
     */
    public static CSSClasses of(String selector) {
        return new CSSClasses(Target.the(selector).locatedBy(selector));
    }

    /**
     * Get CSS classes for a Target element.
     */
    public static CSSClasses of(Target target) {
        return new CSSClasses(target);
    }

    /**
     * Check if the element has a specific class.
     */
    public Question<Boolean> contains(String className) {
        return Question.about("whether " + target + " has class '" + className + "'")
            .answeredBy(actor -> {
                List<String> classes = this.answeredBy(actor);
                return classes.contains(className);
            });
    }

    @Override
    public List<String> answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        String classAttribute = locator.getAttribute("class");
        if (classAttribute == null || classAttribute.isEmpty()) {
            return List.of();
        }
        return Arrays.asList(classAttribute.trim().split("\\s+"));
    }

    @Override
    public String toString() {
        return "CSS classes of " + target;
    }
}
