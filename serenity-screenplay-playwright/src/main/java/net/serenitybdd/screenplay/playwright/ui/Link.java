package net.serenitybdd.screenplay.playwright.ui;

import net.serenitybdd.screenplay.playwright.Target;

/**
 * Factory for locating link (anchor) elements using Playwright selectors.
 *
 * <p>Uses Playwright's role selectors and CSS pseudo-classes for
 * flexible link location.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     Target homeLink = Link.withText("Home");
 *     Target learnMoreLink = Link.containingText("Learn more");
 *     Target helpLink = Link.withTitle("Get help");
 * </pre>
 */
public class Link {

    private Link() {
        // Factory class - prevent instantiation
    }

    /**
     * Locate a link by its exact accessible name (case-insensitive).
     * Uses Playwright's role selector for semantic link matching.
     *
     * @param text The link's accessible name or text content
     * @return A Target for the link element
     */
    public static Target withText(String text) {
        return Target.the("'" + text + "' link")
                .locatedBy("role=link[name=\"" + text + "\" i]");
    }

    /**
     * Locate a link containing the specified text (partial match).
     *
     * @param text The text to search for within the link
     * @return A Target for the link element
     */
    public static Target containingText(String text) {
        return Target.the("link containing '" + text + "'")
                .locatedBy("a:has-text(\"" + text + "\")");
    }

    /**
     * Locate a link by its title attribute (case-insensitive).
     *
     * @param title The title attribute value
     * @return A Target for the link element
     */
    public static Target withTitle(String title) {
        return Target.the("link with title '" + title + "'")
                .locatedBy("a[title=\"" + title + "\" i]");
    }

    /**
     * Locate a link using a custom Playwright selector.
     *
     * @param selector A valid Playwright selector
     * @return A Target for the link element
     */
    public static Target locatedBy(String selector) {
        return Target.the("link").locatedBy(selector);
    }
}
