package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.selectactions.*;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static java.util.Arrays.asList;
import static net.serenitybdd.screenplay.actions.SelectStrategy.*;

public class SelectFromOptions {

    private final SelectStrategy strategy;
    private String[] options;
    private String[] values;
    private Integer[] indexes;

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

    public static SelectFromOptions byIndex(Integer... indexValues) {
        SelectFromOptions selectFromOptions = new SelectFromOptions(ByIndex);
        selectFromOptions.indexes = indexValues;
        return selectFromOptions;
    }

    public Performable from(String cssOrXpathForElement) {
        return from(Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public Performable from(Target target) {
        switch (strategy) {
            case ByValue: return new SelectByValueFromTarget(target, values);
            case ByVisibleText: return new SelectByVisibleTextFromTarget(target, options);
            case ByIndex: return new SelectByIndexFromTarget(target, indexes);
        }
        throw new IllegalStateException("Unknown select strategy " + strategy);
    }

    public Performable from(WebElementFacade element) {
        switch (strategy) {
            case ByValue: return new SelectByValueFromElement(element, values);
            case ByVisibleText: return new SelectByVisibleTextFromElement(element, options);
            case ByIndex: return new SelectByIndexFromElement(element, indexes);
        }
        throw new IllegalStateException("Unknown select strategy " + strategy);
    }

    public Performable from(By... locators) {
        switch (strategy) {
            case ByValue: return new SelectByValueFromBy(asList(values), locators);
            case ByVisibleText: return new SelectByVisibleTextFromBy(asList(options), locators);
            case ByIndex: return new SelectByIndexFromBy(asList(indexes), locators);
        }
        throw new IllegalStateException("Unknown select strategy " + strategy);
    }

}
