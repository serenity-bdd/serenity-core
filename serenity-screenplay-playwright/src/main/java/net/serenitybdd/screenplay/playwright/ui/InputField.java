package net.serenitybdd.screenplay.playwright.ui;

import net.serenitybdd.screenplay.playwright.Target;

/**
 * Factory for locating input field elements using Playwright selectors.
 *
 * <p>Supports various strategies for locating text inputs, including
 * by name/id, placeholder, label association, and aria-label.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     Target emailField = InputField.withNameOrId("email");
 *     Target searchField = InputField.withPlaceholder("Search...");
 *     Target usernameField = InputField.withLabel("Username");
 *     Target passwordField = InputField.withAriaLabel("Enter password");
 * </pre>
 */
public class InputField {

    private InputField() {
        // Factory class - prevent instantiation
    }

    /**
     * Locate an input field by its name attribute, id, or data-test attribute.
     *
     * @param name The name, id, or data-test value
     * @return A Target for the input element
     */
    public static Target withNameOrId(String name) {
        return Target.the("'" + name + "' input field")
                .locatedBy("input#" + name + ", input[name=\"" + name + "\"], input[data-test=\"" + name + "\"]");
    }

    /**
     * Locate an input field by its placeholder text (case-insensitive).
     *
     * @param text The placeholder text
     * @return A Target for the input element
     */
    public static Target withPlaceholder(String text) {
        return Target.the("input field with placeholder '" + text + "'")
                .locatedBy("input[placeholder=\"" + text + "\" i]");
    }

    /**
     * Locate an input field by its associated label text.
     * Supports both label elements with for attribute and aria-label.
     *
     * @param label The label text or aria-label value
     * @return A Target for the input element
     */
    public static Target withLabel(String label) {
        return Target.the("'" + label + "' input field")
                .locatedBy("role=textbox[name=\"" + label + "\" i]");
    }

    /**
     * Locate an input field by its aria-label attribute (case-insensitive).
     *
     * @param label The aria-label value
     * @return A Target for the input element
     */
    public static Target withAriaLabel(String label) {
        return Target.the("input field with aria-label '" + label + "'")
                .locatedBy("input[aria-label=\"" + label + "\" i]");
    }

    /**
     * Locate an input field using a custom Playwright selector.
     *
     * @param selector A valid Playwright selector
     * @return A Target for the input element
     */
    public static Target locatedBy(String selector) {
        return Target.the("input field").locatedBy(selector);
    }
}
