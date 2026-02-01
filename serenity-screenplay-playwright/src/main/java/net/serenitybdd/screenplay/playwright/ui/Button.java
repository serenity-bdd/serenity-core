package net.serenitybdd.screenplay.playwright.ui;

import net.serenitybdd.screenplay.playwright.Target;

/**
 * Factory for locating button elements using Playwright selectors.
 *
 * <p>Uses Playwright's powerful selector syntax including role selectors
 * for accessibility-aware element location.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     Target submitButton = Button.withText("Submit");
 *     Target loginButton = Button.withNameOrId("login-btn");
 *     Target saveButton = Button.withAriaLabel("Save changes");
 *     Target actionButton = Button.containingText("Action");
 * </pre>
 *
 * @see <a href="https://playwright.dev/java/docs/locators#locate-by-role">Playwright Role Selectors</a>
 */
public class Button {

    private Button() {
        // Factory class - prevent instantiation
    }

    /**
     * Locate a button by its exact accessible name (case-insensitive).
     * Uses Playwright's role selector for semantic button matching.
     *
     * @param text The button's accessible name or text content
     * @return A Target for the button element
     */
    public static Target withText(String text) {
        return Target.the("'" + text + "' button")
                .locatedBy("role=button[name=\"" + text + "\" i]");
    }

    /**
     * Locate a button by its name attribute, id, or data-test attribute.
     *
     * @param name The name, id, or data-test value
     * @return A Target for the button element
     */
    public static Target withNameOrId(String name) {
        return Target.the("'" + name + "' button")
                .locatedBy("button#" + name + ", button[name=\"" + name + "\"], button[data-test=\"" + name + "\"]");
    }

    /**
     * Locate a button by its aria-label attribute (case-insensitive).
     *
     * @param label The aria-label value
     * @return A Target for the button element
     */
    public static Target withAriaLabel(String label) {
        return Target.the("button with aria-label '" + label + "'")
                .locatedBy("button[aria-label=\"" + label + "\" i]");
    }

    /**
     * Locate a button containing the specified text (partial match).
     *
     * @param text The text to search for within the button
     * @return A Target for the button element
     */
    public static Target containingText(String text) {
        return Target.the("button containing '" + text + "'")
                .locatedBy("button:has-text(\"" + text + "\")");
    }

    /**
     * Locate a button using a custom Playwright selector.
     *
     * @param selector A valid Playwright selector
     * @return A Target for the button element
     */
    public static Target locatedBy(String selector) {
        return Target.the("button").locatedBy(selector);
    }
}
