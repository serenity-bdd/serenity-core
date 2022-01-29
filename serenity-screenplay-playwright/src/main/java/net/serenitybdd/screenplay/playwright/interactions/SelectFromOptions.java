package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.interactions.selectactions.SelectByIndexFromTarget;
import net.serenitybdd.screenplay.playwright.interactions.selectactions.SelectByValueFromTarget;
import net.serenitybdd.screenplay.playwright.interactions.selectactions.SelectByVisibleTextFromTarget;

import static net.serenitybdd.screenplay.playwright.interactions.SelectStrategy.*;

/**
 * This method waits for an element matching selector, waits for actionability checks, waits until all specified options are present in the <select> element and selects these options.
 * If the target element is not a <select> element, this method throws an error. However, if the element is inside the <label> element that has an associated control, the control will be used instead.
 * Returns the array of option values that have been successfully selected.
 * Triggers a change and input event once all the provided options have been selected.
 * <p>
 * Sample usage:
 * <pre>
 *     SelectFromOptions.byValue("Option 1").from("#dropdown");
 * </pre>
 */
public class SelectFromOptions {

    private final SelectStrategy strategy;
    private String[] options;
    private String[] values;
    private String[] indexes;

    public SelectFromOptions(SelectStrategy strategy) {
        this.strategy = strategy;
    }

    public static SelectFromOptions byValue(String... values) {
        SelectFromOptions selectFromOptions = new SelectFromOptions(ByValue);
        selectFromOptions.values = values;
        return selectFromOptions;
    }

    public static SelectFromOptions byVisibleText(String... visibleTexts) {
        SelectFromOptions selectFromOptions = new SelectFromOptions(ByVisibleText);
        selectFromOptions.options = visibleTexts;
        return selectFromOptions;
    }

    public static SelectFromOptions byIndex(String... indexValues) {
        SelectFromOptions selectFromOptions = new SelectFromOptions(ByIndex);
        selectFromOptions.indexes = indexValues;
        return selectFromOptions;
    }

    public Performable from(String cssOrXpathForElement) {
        return from(Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public Performable from(Target target) {
        switch (strategy) {
            case ByValue:
                return new SelectByValueFromTarget(target, values);
            case ByVisibleText:
                return new SelectByVisibleTextFromTarget(target, options);
            case ByIndex:
                return new SelectByIndexFromTarget(target, indexes);
        }
        throw new IllegalStateException("Unknown select strategy " + strategy);
    }
}
