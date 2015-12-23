package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.targets.Target;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.selectactions.*;

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
            case ByValue: return new SelectByValueFromTarget(target, theText);
            case ByVisibleText: return new SelectByVisibleTextFromTarget(target, theText);
            case ByIndex: return new SelectByIndexFromTarget(target, indexValue);
        }
        throw new IllegalStateException("Unknown select strategy " + strategy);
    }

    public Performable from(WebElementFacade element) {
        switch (strategy) {
            case ByValue: return new SelectByValueFromElement(element, theText);
            case ByVisibleText: return new SelectByVisibleTextFromElement(element, theText);
            case ByIndex: return new SelectByIndexFromElement(element, indexValue);
        }
        throw new IllegalStateException("Unknown select strategy " + strategy);
    }
}
