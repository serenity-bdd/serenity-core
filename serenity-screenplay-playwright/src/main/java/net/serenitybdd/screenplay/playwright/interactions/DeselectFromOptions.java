package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.interactions.deselectactions.DeselectAllFromTarget;
import net.serenitybdd.screenplay.playwright.interactions.deselectactions.DeselectByIndexFromTarget;
import net.serenitybdd.screenplay.playwright.interactions.deselectactions.DeselectByValueFromTarget;
import net.serenitybdd.screenplay.playwright.interactions.deselectactions.DeselectByVisibleTextFromTarget;

import static net.serenitybdd.screenplay.playwright.interactions.DeselectStrategy.*;

/**
 * Deselect options from a multi-select dropdown by text, value or index.
 * This is the inverse of {@link SelectFromOptions} for multi-select elements.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // Deselect by value
 *     actor.attemptsTo(DeselectFromOptions.byValue("option1", "option2").from("#multi-select"));
 *
 *     // Deselect by visible text
 *     actor.attemptsTo(DeselectFromOptions.byVisibleText("Option Label").from(DROPDOWN));
 *
 *     // Deselect by index
 *     actor.attemptsTo(DeselectFromOptions.byIndex(0).from("#multi-select"));
 *
 *     // Deselect all options
 *     actor.attemptsTo(DeselectFromOptions.all().from("#multi-select"));
 * </pre>
 */
public class DeselectFromOptions {

    private final DeselectStrategy strategy;
    private String option;
    private String[] values;
    private int index;

    public DeselectFromOptions(DeselectStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Deselect options by their value attributes.
     *
     * @param values The values to deselect
     * @return A DeselectFromOptions builder
     */
    public static DeselectFromOptions byValue(String... values) {
        DeselectFromOptions deselectFromOptions = new DeselectFromOptions(ByValue);
        deselectFromOptions.values = values;
        return deselectFromOptions;
    }

    /**
     * Deselect an option by its visible text.
     *
     * @param visibleText The visible text of the option to deselect
     * @return A DeselectFromOptions builder
     */
    public static DeselectFromOptions byVisibleText(String visibleText) {
        DeselectFromOptions deselectFromOptions = new DeselectFromOptions(ByVisibleText);
        deselectFromOptions.option = visibleText;
        return deselectFromOptions;
    }

    /**
     * Deselect an option by its index (0-based).
     *
     * @param indexValue The index of the option to deselect
     * @return A DeselectFromOptions builder
     */
    public static DeselectFromOptions byIndex(int indexValue) {
        DeselectFromOptions deselectFromOptions = new DeselectFromOptions(ByIndex);
        deselectFromOptions.index = indexValue;
        return deselectFromOptions;
    }

    /**
     * Deselect all currently selected options.
     *
     * @return A builder that will deselect all options
     */
    public static DeselectAll all() {
        return new DeselectAll();
    }

    /**
     * Specify the dropdown to deselect from using a CSS or XPath selector.
     *
     * @param cssOrXpathForElement The selector for the dropdown
     * @return A Performable that deselects the specified options
     */
    public Performable from(String cssOrXpathForElement) {
        return from(Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    /**
     * Specify the dropdown to deselect from using a Target.
     *
     * @param target The Target for the dropdown
     * @return A Performable that deselects the specified options
     */
    public Performable from(Target target) {
        switch (strategy) {
            case ByValue:
                return new DeselectByValueFromTarget(target, values);
            case ByVisibleText:
                return new DeselectByVisibleTextFromTarget(target, option);
            case ByIndex:
                return new DeselectByIndexFromTarget(target, index);
        }
        throw new IllegalStateException("Unknown deselect strategy " + strategy);
    }

    /**
     * Builder for deselecting all options.
     */
    public static class DeselectAll {
        /**
         * Specify the dropdown to deselect all options from using a selector.
         */
        public Performable from(String cssOrXpathForElement) {
            return from(Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
        }

        /**
         * Specify the dropdown to deselect all options from using a Target.
         */
        public Performable from(Target target) {
            return new DeselectAllFromTarget(target);
        }
    }
}
