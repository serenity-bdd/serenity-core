package net.serenitybdd.screenplay.playwright.ui;

import net.serenitybdd.screenplay.playwright.Target;

/**
 * Factory for locating checkbox elements using Playwright selectors.
 *
 * <p>Uses Playwright's role selectors for accessibility-aware checkbox location.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     Target termsCheckbox = Checkbox.withLabel("I agree to the terms");
 *     Target rememberMe = Checkbox.withNameOrId("remember-me");
 *     Target optionA = Checkbox.withValue("option-a");
 * </pre>
 */
public class Checkbox {

    private Checkbox() {
        // Factory class - prevent instantiation
    }

    /**
     * Locate a checkbox by its label text (case-insensitive).
     * Uses Playwright's role selector for semantic checkbox matching.
     *
     * @param label The checkbox's label or accessible name
     * @return A Target for the checkbox element
     */
    public static Target withLabel(String label) {
        return Target.the("'" + label + "' checkbox")
                .locatedBy("role=checkbox[name=\"" + label + "\" i]");
    }

    /**
     * Locate a checkbox by its name attribute, id, or data-test attribute.
     *
     * @param name The name, id, or data-test value
     * @return A Target for the checkbox element
     */
    public static Target withNameOrId(String name) {
        return Target.the("'" + name + "' checkbox")
                .locatedBy("input[type=\"checkbox\"]#" + name + ", input[type=\"checkbox\"][name=\"" + name + "\"], input[type=\"checkbox\"][data-test=\"" + name + "\"]");
    }

    /**
     * Locate a checkbox by its value attribute.
     *
     * @param value The value attribute
     * @return A Target for the checkbox element
     */
    public static Target withValue(String value) {
        return Target.the("checkbox with value '" + value + "'")
                .locatedBy("input[type=\"checkbox\"][value=\"" + value + "\"]");
    }

    /**
     * Locate a checkbox using a custom Playwright selector.
     *
     * @param selector A valid Playwright selector
     * @return A Target for the checkbox element
     */
    public static Target locatedBy(String selector) {
        return Target.the("checkbox").locatedBy(selector);
    }
}
