package net.serenitybdd.screenplay.tasks;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import net.thucydides.core.annotations.Step;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class SelectFromOptions implements Performable {

    enum SelectStrategy {
        ByValue, ByVisibleText, ByIndex
    }

    private final SelectStrategy strategy;
    private String theText;
    private Integer indexValue;
    private Target target;

    public SelectFromOptions(SelectStrategy strategy) {
        this.strategy = strategy;
    }

    public static SelectFromOptions byValue(String value) {
        SelectFromOptions enterAction = instrumented(SelectFromOptions.class, SelectStrategy.ByValue);
        enterAction.theText = value;
        return enterAction;
    }

    public static SelectFromOptions byVisibleText(String value) {
        SelectFromOptions enterAction = instrumented(SelectFromOptions.class, SelectStrategy.ByVisibleText);
        enterAction.theText = value;
        return enterAction;
    }

    public static SelectFromOptions byIndex(Integer indexValue) {
        SelectFromOptions enterAction = instrumented(SelectFromOptions.class, SelectStrategy.ByVisibleText);
        enterAction.indexValue = indexValue;
        return enterAction;
    }

    public Performable from(String cssOrXpathForElement) {
        this.target = Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement);
        return this;
    }

    public Performable from(Target target) {
        this.target = target;
        return this;
    }

    @Step("{0} selects '#theText' from #target")
    public <T extends Actor> void performAs(T theUser) {
        WebElementFacade targetDropdown = BrowseTheWeb.as(theUser).moveTo(target.getCssOrXPathSelector());
        switch (strategy) {
            case ByValue:
                targetDropdown.selectByVisibleText(theText);
                break;
            case ByVisibleText:
                targetDropdown.selectByVisibleText(theText);
                break;
            case ByIndex:
                targetDropdown.selectByIndex(indexValue);
                break;
        }
    }
}
