package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.deselectactions.DeselectByIndexFromTarget;
import net.serenitybdd.screenplay.actions.deselectactions.DeselectByValueFromTarget;
import net.serenitybdd.screenplay.actions.deselectactions.DeselectByVisibleTextFromTarget;
import net.serenitybdd.screenplay.targets.Target;

import static net.serenitybdd.screenplay.Tasks.instrumented;

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
            case ByValue: return instrumented(DeselectByValueFromTarget.class, target, theText);
            case ByVisibleText: return instrumented(DeselectByVisibleTextFromTarget.class, target, theText);
            case ByIndex: return instrumented(DeselectByIndexFromTarget.class, target, indexValue);
        }
        throw new IllegalStateException("Unknown select strategy " + strategy);
    }

}
