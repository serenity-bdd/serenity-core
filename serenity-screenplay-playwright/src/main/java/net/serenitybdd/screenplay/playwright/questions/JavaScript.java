package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.Arrays;

/**
 * Evaluate JavaScript and return the result.
 *
 * <p>Playwright's evaluate method passes arguments directly to the expression.
 * For multiple arguments, use an arrow function with array destructuring:</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // No arguments
 *     String title = actor.asksFor(JavaScript.evaluate("document.title"));
 *
 *     // Single argument
 *     String value = actor.asksFor(JavaScript.evaluate("arg => document.getElementById(arg).value")
 *         .withArguments("email"));
 *
 *     // Multiple arguments (use array destructuring)
 *     Object result = actor.asksFor(JavaScript.evaluate("([a, b]) => a * b")
 *         .withArguments(6, 7));
 * </pre>
 */
public class JavaScript<T> implements Question<T> {

    private final String script;
    private Object[] arguments = new Object[0];

    private JavaScript(String script) {
        this.script = script;
    }

    /**
     * Evaluate JavaScript and return the result.
     */
    @SuppressWarnings("unchecked")
    public static <T> JavaScript<T> evaluate(String script) {
        return new JavaScript<>(script);
    }

    /**
     * Provide arguments to the script.
     * Single argument is available directly, multiple arguments are passed as an array.
     */
    public JavaScript<T> withArguments(Object... args) {
        this.arguments = args;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        if (arguments.length == 1) {
            return (T) page.evaluate(script, arguments[0]);
        } else if (arguments.length > 1) {
            return (T) page.evaluate(script, Arrays.asList(arguments));
        }
        return (T) page.evaluate(script);
    }

    @Override
    public String toString() {
        return "the result of JavaScript: " + script;
    }
}
