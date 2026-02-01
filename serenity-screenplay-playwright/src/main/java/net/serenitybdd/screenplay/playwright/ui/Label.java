package net.serenitybdd.screenplay.playwright.ui;

import net.serenitybdd.screenplay.playwright.Target;

/**
 * Factory for locating label elements using Playwright selectors.
 *
 * <p>Useful for finding labels associated with form fields or as standalone text elements.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     Target usernameLabel = Label.withText("Username");
 *     Target exactLabel = Label.withExactText("Email Address");
 *     Target emailLabel = Label.forFieldId("email");
 * </pre>
 */
public class Label {

    private Label() {
        // Factory class - prevent instantiation
    }

    /**
     * Locate a label containing the specified text (partial match).
     *
     * @param text The text to search for within the label
     * @return A Target for the label element
     */
    public static Target withText(String text) {
        return Target.the("'" + text + "' label")
                .locatedBy("label:has-text(\"" + text + "\")");
    }

    /**
     * Locate a label with exact text content.
     *
     * @param text The exact text content
     * @return A Target for the label element
     */
    public static Target withExactText(String text) {
        return Target.the("label with exact text '" + text + "'")
                .locatedBy("label:text-is(\"" + text + "\")");
    }

    /**
     * Locate a label by its 'for' attribute (field association).
     *
     * @param fieldId The id of the associated form field
     * @return A Target for the label element
     */
    public static Target forFieldId(String fieldId) {
        return Target.the("label for '" + fieldId + "'")
                .locatedBy("label[for=\"" + fieldId + "\"]");
    }

    /**
     * Locate a label using a custom Playwright selector.
     *
     * @param selector A valid Playwright selector
     * @return A Target for the label element
     */
    public static Target locatedBy(String selector) {
        return Target.the("label").locatedBy(selector);
    }
}
