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
    private Integer indexValue;

    public SelectFromOptions(SelectStrategy strategy) {
        this.strategy = strategy;
    }

    public static SelectFromOptions byValue(String value) {
        SelectFromOptions enterAction = new SelectFromOptions(SelectStrategy.ByValue);
        enterAction.theText = value;
        return enterAction;
    }

    public static SelectFromOptions byVisibleText(String value) {
        SelectFromOptions enterAction = new SelectFromOptions(SelectStrategy.ByVisibleText);
        enterAction.theText = value;
        return enterAction;
    }

    public static SelectFromOptions byIndex(Integer indexValue) {
        SelectFromOptions enterAction = new SelectFromOptions( SelectStrategy.ByIndex);
        enterAction.indexValue = indexValue;
        return enterAction;
    }

    public Performable from(String cssOrXpathForElement) {
        return from(Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public Performable from(Target target) {
        switch (strategy) {
            case ByValue: return instrumented(SelectByValueFromTarget.class, target, theText);
            case ByVisibleText: return instrumented(SelectByVisibleTextFromTarget.class, target, theText);
            case ByIndex: return instrumented(SelectByIndexFromTarget.class, target, indexValue);
        }
        throw new IllegalStateException("Unknown select strategy " + strategy);
    }

    public Performable from(WebElementFacade element) {
        switch (strategy) {
            case ByValue: return instrumented(SelectByValueFromElement.class, element, theText);
            case ByVisibleText: return instrumented(SelectByVisibleTextFromElement.class, element, theText);
            case ByIndex: return instrumented(SelectByIndexFromElement.class, element, indexValue);
        }
        throw new IllegalStateException("Unknown select strategy " + strategy);
    }

    public Performable from(By... locators) {
        switch (strategy) {
            case ByValue: return instrumented(SelectByValueFromBy.class, theText, locators);
            case ByVisibleText: return instrumented(SelectByVisibleTextFromBy.class, theText, locators);
            case ByIndex: return instrumented(SelectByIndexFromBy.class, indexValue, locators);
        }
        throw new IllegalStateException("Unknown select strategy " + strategy);
    }

}
