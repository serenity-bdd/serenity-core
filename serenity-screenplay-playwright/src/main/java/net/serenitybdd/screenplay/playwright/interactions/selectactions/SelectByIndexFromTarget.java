package net.serenitybdd.screenplay.playwright.interactions.selectactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.thucydides.core.annotations.Step;

public class SelectByIndexFromTarget implements Interaction {
    private final Target target;
    private final String[] indexes;

    public SelectByIndexFromTarget(Target target, String... indexes) {
        this.target = target;
        this.indexes = indexes;
    }

    @Step("{0} selects index #index in #target")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWebWithPlaywright.as(theUser).getCurrentPage().selectOption(target.asSelector(), indexes);
        BrowseTheWebWithPlaywright.as(theUser).notifyScreenChange();
    }
}
