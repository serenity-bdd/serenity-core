package net.serenitybdd.screenplay.playwright.ui;

import net.serenitybdd.screenplay.playwright.Target;

/**
 * Factory for locating radio button elements using Playwright selectors.
 *
 * <p>Uses Playwright's role selectors for accessibility-aware radio button location.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     Target yesOption = RadioButton.withLabel("Yes");
 *     Target genderMale = RadioButton.withNameOrId("gender-male");
 *     Target optionB = RadioButton.withValue("option-b");
 * </pre>
 */
public class RadioButton {

    private RadioButton() {
        // Factory class - prevent instantiation
    }

    /**
     * Locate a radio button by its label text (case-insensitive).
     * Uses Playwright's role selector for semantic radio matching.
     *
     * @param label The radio button's label or accessible name
     * @return A Target for the radio button element
     */
    public static Target withLabel(String label) {
        return Target.the("'" + label + "' radio button")
                .locatedBy("role=radio[name=\"" + label + "\" i]");
    }

    /**
     * Locate a radio button by its name attribute, id, or data-test attribute.
     *
     * @param name The name, id, or data-test value
     * @return A Target for the radio button element
     */
    public static Target withNameOrId(String name) {
        return Target.the("'" + name + "' radio button")
                .locatedBy("input[type=\"radio\"]#" + name + ", input[type=\"radio\"][name=\"" + name + "\"], input[type=\"radio\"][data-test=\"" + name + "\"]");
    }

    /**
     * Locate a radio button by its value attribute.
     *
     * @param value The value attribute
     * @return A Target for the radio button element
     */
    public static Target withValue(String value) {
        return Target.the("radio button with value '" + value + "'")
                .locatedBy("input[type=\"radio\"][value=\"" + value + "\"]");
    }

    /**
     * Locate a radio button using a custom Playwright selector.
     *
     * @param selector A valid Playwright selector
     * @return A Target for the radio button element
     */
    public static Target locatedBy(String selector) {
        return Target.the("radio button").locatedBy(selector);
    }
}
