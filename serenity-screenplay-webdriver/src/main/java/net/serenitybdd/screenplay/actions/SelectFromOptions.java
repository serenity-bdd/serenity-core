package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.selectactions.*;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class SelectFromOptions {

    private final SelectStrategy strategy;
    private String theText;
    private String theValue;
    private Integer indexValue;

    public SelectFromOptions(SelectStrategy strategy) {
        this.strategy = strategy;
    }

    public static SelectFromOptions byValue(String value) {
        SelectFromOptions selectFromOptions = new SelectFromOptions(SelectStrategy.ByValue);
        selectFromOptions.theValue = value;
        return selectFromOptions;
    }

    public static SelectFromOptions byVisibleText(String visibleText) {
        SelectFromOptions selectFromOptions = new SelectFromOptions(SelectStrategy.ByVisibleText);
        selectFromOptions.theText = visibleText;
        return selectFromOptions;
    }

    public static SelectFromOptions byIndex(Integer indexValue) {
        SelectFromOptions selectFromOptions = new SelectFromOptions( SelectStrategy.ByIndex);
        selectFromOptions.indexValue = indexValue;
        return selectFromOptions;
    }

    public Performable from(String cssOrXpathForElement) {
        return from(Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public Performable from(Target target) {
        switch (strategy) {
            case ByValue: return instrumented(SelectByValueFromTarget.class, target, theValue);
            case ByVisibleText: return instrumented(SelectByVisibleTextFromTarget.class, target, theText);
            case ByIndex: return instrumented(SelectByIndexFromTarget.class, target, indexValue);
        }
        throw new IllegalStateException("Unknown select strategy " + strategy);
    }

    public Performable from(WebElementFacade element) {
        switch (strategy) {
            case ByValue: return instrumented(SelectByValueFromElement.class, element, theValue);
            case ByVisibleText: return instrumented(SelectByVisibleTextFromElement.class, element, theText);
            case ByIndex: return instrumented(SelectByIndexFromElement.class, element, indexValue);
        }
        throw new IllegalStateException("Unknown select strategy " + strategy);
    }

    public Performable from(By... locators) {
        switch (strategy) {
            case ByValue: return instrumented(SelectByValueFromBy.class, theValue, locators);
            case ByVisibleText: return instrumented(SelectByVisibleTextFromBy.class, theText, locators);
            case ByIndex: return instrumented(SelectByIndexFromBy.class, indexValue, locators);
        }
        throw new IllegalStateException("Unknown select strategy " + strategy);
    }

}
