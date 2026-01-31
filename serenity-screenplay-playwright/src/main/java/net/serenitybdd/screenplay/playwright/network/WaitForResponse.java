package net.serenitybdd.screenplay.playwright.network;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Wait for a network response matching a pattern while performing an action.
 *
 * <p>This interaction uses Playwright's waitForResponse which expects an action
 * that triggers the response. Use it with whilePerforming() to specify the action.</p>
 */
public class WaitForResponse implements Performable {

    private final Predicate<Response> responseMatcher;
    private final String description;
    private Performable triggeringAction;
    private Double timeoutMs;

    private WaitForResponse(Predicate<Response> responseMatcher, String description) {
        this.responseMatcher = responseMatcher;
        this.description = description;
    }

    /**
     * Wait for a response where the URL matches the glob pattern.
     */
    public static WaitForResponse matching(String globPattern) {
        String regex = globToRegex(globPattern);
        Pattern pattern = Pattern.compile(regex);
        return new WaitForResponse(
            response -> pattern.matcher(response.url()).matches(),
            globPattern
        );
    }

    /**
     * Wait for a response where the URL matches the regex pattern.
     */
    public static WaitForResponse matching(Pattern pattern) {
        return new WaitForResponse(
            response -> pattern.matcher(response.url()).matches(),
            pattern.pattern()
        );
    }

    /**
     * Wait for a response where the URL contains the substring.
     */
    public static WaitForResponse containingUrl(String substring) {
        return new WaitForResponse(
            response -> response.url().contains(substring),
            "URL containing '" + substring + "'"
        );
    }

    /**
     * Specify the action that triggers the response.
     * The action will be performed while waiting for the response.
     */
    public WaitForResponse whilePerforming(Performable action) {
        this.triggeringAction = action;
        return this;
    }

    /**
     * Set a custom timeout in milliseconds.
     */
    public WaitForResponse withTimeout(double timeoutMs) {
        this.timeoutMs = timeoutMs;
        return this;
    }

    @Override
    @Step("{0} waits for response matching #description")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();

        Page.WaitForResponseOptions options = new Page.WaitForResponseOptions();
        if (timeoutMs != null) {
            options.setTimeout(timeoutMs);
        }

        if (triggeringAction != null) {
            // Wait for response while performing the action
            page.waitForResponse(responseMatcher, options, () -> {
                triggeringAction.performAs(actor);
            });
        } else {
            // No action specified - wait for next matching response
            // This uses an empty action, which means it will wait for a response
            // that's already in flight or triggered by a previous action
            page.waitForResponse(responseMatcher, options, () -> {});
        }
    }

    /**
     * Convert a glob pattern to a regex pattern.
     */
    private static String globToRegex(String glob) {
        StringBuilder regex = new StringBuilder();
        for (int i = 0; i < glob.length(); i++) {
            char c = glob.charAt(i);
            switch (c) {
                case '*':
                    if (i + 1 < glob.length() && glob.charAt(i + 1) == '*') {
                        regex.append(".*");
                        i++; // Skip next *
                    } else {
                        regex.append("[^/]*");
                    }
                    break;
                case '?':
                    regex.append(".");
                    break;
                case '.':
                case '(':
                case ')':
                case '+':
                case '|':
                case '^':
                case '$':
                case '@':
                case '%':
                case '{':
                case '}':
                case '[':
                case ']':
                case '\\':
                    regex.append("\\").append(c);
                    break;
                default:
                    regex.append(c);
            }
        }
        return regex.toString();
    }
}
