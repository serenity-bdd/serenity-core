package net.serenitybdd.screenplay.playwright.interactions.selectactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.thucydides.core.annotations.Step;

public class SelectByValueFromTarget implements Performable {
    private final Target target;
    private final String[] values;

    public SelectByValueFromTarget(Target target, String... values) {
        this.target = target;
        this.values = values;
    }

    @Step("{0} selects #values in #target")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWebWithPlaywright.as(theUser).getCurrentPage().selectOption(target.asSelector(), values);
        BrowseTheWebWithPlaywright.as(theUser).notifyScreenChange();
    }
}
