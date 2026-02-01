package net.serenitybdd.screenplay.playwright.ui;

import net.serenitybdd.screenplay.playwright.Target;

/**
 * Factory for locating dropdown (select) elements using Playwright selectors.
 *
 * <p>Uses Playwright's role selectors for accessibility-aware dropdown location.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     Target countryDropdown = Dropdown.withLabel("Country");
 *     Target stateDropdown = Dropdown.withNameOrId("state");
 * </pre>
 */
public class Dropdown {

    private Dropdown() {
        // Factory class - prevent instantiation
    }

    /**
     * Locate a dropdown by its label text (case-insensitive).
     * Uses Playwright's role selector for semantic combobox matching.
     *
     * @param label The dropdown's label or accessible name
     * @return A Target for the dropdown element
     */
    public static Target withLabel(String label) {
        return Target.the("'" + label + "' dropdown")
                .locatedBy("role=combobox[name=\"" + label + "\" i]");
    }

    /**
     * Locate a dropdown by its name attribute, id, or data-test attribute.
     *
     * @param name The name, id, or data-test value
     * @return A Target for the dropdown element
     */
    public static Target withNameOrId(String name) {
        return Target.the("'" + name + "' dropdown")
                .locatedBy("select#" + name + ", select[name=\"" + name + "\"], select[data-test=\"" + name + "\"]");
    }

    /**
     * Locate a dropdown using a custom Playwright selector.
     *
     * @param selector A valid Playwright selector
     * @return A Target for the dropdown element
     */
    public static Target locatedBy(String selector) {
        return Target.the("dropdown").locatedBy(selector);
    }
}
