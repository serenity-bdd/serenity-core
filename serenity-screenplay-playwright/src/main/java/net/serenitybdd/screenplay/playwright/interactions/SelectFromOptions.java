package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.interactions.selectactions.SelectByIndexFromTarget;
import net.serenitybdd.screenplay.playwright.interactions.selectactions.SelectByValueFromTarget;
import net.serenitybdd.screenplay.playwright.interactions.selectactions.SelectByVisibleTextFromTarget;

import static net.serenitybdd.screenplay.playwright.interactions.SelectStrategy.*;

/**
 * Select an option from dropdown by text, value or index.
 * More info: https://playwright.dev/java/docs/api/class-page/#page-select-option.
 * <p>
 * Sample usage:
 * <pre>
 *     SelectFromOptions.byValue("Option 1").from("#dropdown");
 * </pre>
 */
public class SelectFromOptions {

    private final SelectStrategy strategy;
    private String option;
    private String[] values;
    private int index;

    public SelectFromOptions(SelectStrategy strategy) {
        this.strategy = strategy;
    }

    public static SelectFromOptions byValue(String... values) {
        SelectFromOptions selectFromOptions = new SelectFromOptions(ByValue);
        selectFromOptions.values = values;
        return selectFromOptions;
    }

    public static SelectFromOptions byVisibleText(String visibleText) {
        SelectFromOptions selectFromOptions = new SelectFromOptions(ByVisibleText);
        selectFromOptions.option = visibleText;
        return selectFromOptions;
    }

    public static SelectFromOptions byIndex(int indexValue) {
        SelectFromOptions selectFromOptions = new SelectFromOptions(ByIndex);
        selectFromOptions.index = indexValue;
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
                return new SelectByVisibleTextFromTarget(target, option);
            case ByIndex:
                return new SelectByIndexFromTarget(target, index);
        }
        throw new IllegalStateException("Unknown select strategy " + strategy);
    }
}
