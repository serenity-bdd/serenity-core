package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.Arrays;

/**
 * Execute JavaScript on the current page.
 *
 * <p>Playwright's evaluate method passes arguments directly to the expression.
 * For multiple arguments, use an arrow function with array destructuring:</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // No arguments
 *     actor.attemptsTo(
 *         ExecuteJavaScript.async("window.scrollTo(0, document.body.scrollHeight)")
 *     );
 *
 *     // Single argument (available as 'arg')
 *     actor.attemptsTo(
 *         ExecuteJavaScript.async("arg => document.getElementById(arg).click()")
 *             .withArguments("myButton")
 *     );
 *
 *     // Multiple arguments (use array destructuring)
 *     actor.attemptsTo(
 *         ExecuteJavaScript.async("([a, b]) => document.title = a + ' ' + b")
 *             .withArguments("Hello", "World")
 *     );
 * </pre>
 */
public class ExecuteJavaScript implements Performable {

    private final String script;
    private Object[] arguments = new Object[0];

    private ExecuteJavaScript(String script) {
        this.script = script;
    }

    /**
     * Execute JavaScript asynchronously.
     */
    public static ExecuteJavaScript async(String script) {
        return new ExecuteJavaScript(script);
    }

    /**
     * Provide arguments to the script.
     * Single argument is available directly, multiple arguments are passed as an array.
     */
    public ExecuteJavaScript withArguments(Object... args) {
        this.arguments = args;
        return this;
    }

    @Override
    @Step("{0} executes JavaScript")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        if (arguments.length == 1) {
            page.evaluate(script, arguments[0]);
        } else if (arguments.length > 1) {
            page.evaluate(script, Arrays.asList(arguments));
        } else {
            page.evaluate(script);
        }
        BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
    }
}
