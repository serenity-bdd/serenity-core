package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.deselectactions.DeselectByIndexFromTarget;
import net.serenitybdd.screenplay.actions.deselectactions.DeselectByValueFromTarget;
import net.serenitybdd.screenplay.actions.deselectactions.DeselectByVisibleTextFromTarget;
import net.serenitybdd.screenplay.targets.Target;

public class DeselectFromOptions {

    private final SelectStrategy strategy;
    private String theText;
    private Integer indexValue;

    public DeselectFromOptions(SelectStrategy strategy) {
        this.strategy = strategy;
    }

    public static DeselectFromOptions byValue(String value) {
        DeselectFromOptions enterAction = new DeselectFromOptions(SelectStrategy.ByValue);
        enterAction.theText = value;
        return enterAction;
    }

    public static DeselectFromOptions byVisibleText(String value) {
        DeselectFromOptions enterAction = new DeselectFromOptions(SelectStrategy.ByVisibleText);
        enterAction.theText = value;
        return enterAction;
    }

    public static DeselectFromOptions byIndex(Integer indexValue) {
        DeselectFromOptions enterAction = new DeselectFromOptions( SelectStrategy.ByIndex);
        enterAction.indexValue = indexValue;
        return enterAction;
    }

    public Performable from(String cssOrXpathForElement) {
        return from(Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public Performable from(Target target) {
        switch (strategy) {
            case ByValue: return new DeselectByValueFromTarget(target, theText);
            case ByVisibleText: return new DeselectByVisibleTextFromTarget(target, theText);
            case ByIndex: return new DeselectByIndexFromTarget(target, indexValue);
        }
        throw new IllegalStateException("Unknown select strategy " + strategy);
    }

    public static Performable clear(Target dropdownList) {
        return null;
    }

}
