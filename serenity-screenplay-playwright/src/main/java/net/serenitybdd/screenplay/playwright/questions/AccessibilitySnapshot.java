package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Retrieve accessibility information from the page or a specific element.
 *
 * <p>This question provides accessibility details using Playwright's ARIA snapshot
 * functionality, which is useful for testing accessibility compliance.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // Get accessibility info for the entire page
 *     String snapshot = actor.asksFor(AccessibilitySnapshot.ofThePage());
 *
 *     // Get accessibility info for a specific element
 *     String snapshot = actor.asksFor(AccessibilitySnapshot.of("#main-nav"));
 *     String snapshot = actor.asksFor(AccessibilitySnapshot.of(NAVIGATION));
 *
 *     // Get all elements with a specific ARIA role
 *     List&lt;String&gt; buttons = actor.asksFor(AccessibilitySnapshot.allWithRole(AriaRole.BUTTON));
 * </pre>
 *
 * @see <a href="https://playwright.dev/java/docs/accessibility-testing">Playwright Accessibility Testing</a>
 */
public class AccessibilitySnapshot implements Question<String> {

    private final Target target;
    private final boolean fullPage;

    private AccessibilitySnapshot(Target target, boolean fullPage) {
        this.target = target;
        this.fullPage = fullPage;
    }

    /**
     * Get the accessibility snapshot of the entire page.
     * Returns a YAML-like representation of the accessibility tree.
     */
    public static AccessibilitySnapshot ofThePage() {
        return new AccessibilitySnapshot(null, true);
    }

    /**
     * Get the accessibility snapshot of a specific element.
     *
     * @param selector The CSS or XPath selector for the element
     */
    public static AccessibilitySnapshot of(String selector) {
        return new AccessibilitySnapshot(Target.the(selector).locatedBy(selector), false);
    }

    /**
     * Get the accessibility snapshot of a specific Target element.
     *
     * @param target The Target element
     */
    public static AccessibilitySnapshot of(Target target) {
        return new AccessibilitySnapshot(target, false);
    }

    /**
     * Get all elements with the specified ARIA role.
     *
     * @param role The ARIA role to search for
     * @return A Question that returns a list of accessible names for elements with the role
     */
    public static Question<List<String>> allWithRole(AriaRole role) {
        return new AccessibleNamesWithRole(role);
    }

    @Override
    public String answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();

        if (fullPage) {
            // Get ARIA snapshot of the entire page
            return page.locator("body").ariaSnapshot();
        } else {
            // Get ARIA snapshot of specific element
            Locator locator = target.resolveFor(page);
            return locator.ariaSnapshot();
        }
    }

    @Override
    public String toString() {
        if (fullPage) {
            return "accessibility snapshot of the page";
        }
        return "accessibility snapshot of " + target;
    }
}

/**
 * Question to get all accessible names for elements with a specific ARIA role.
 */
class AccessibleNamesWithRole implements Question<List<String>> {
    private final AriaRole role;

    AccessibleNamesWithRole(AriaRole role) {
        this.role = role;
    }

    @Override
    public List<String> answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = page.getByRole(role);

        return locator.all().stream()
            .map(element -> element.getAttribute("aria-label") != null
                ? element.getAttribute("aria-label")
                : element.textContent().trim())
            .filter(name -> !name.isEmpty())
            .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "accessible names of all elements with role " + role;
    }
}
