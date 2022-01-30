package net.serenitybdd.screenplay.playwright.interactions.selectactions;

import com.microsoft.playwright.options.SelectOption;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.thucydides.core.annotations.Step;

public class SelectByIndexFromTarget implements Interaction {
    private final Target target;
    private final int index;

    public SelectByIndexFromTarget(Target target, int index) {
        this.target = target;
        this.index = index;
    }

    @Step("{0} selects index #index in #target")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWebWithPlaywright.as(theUser).getCurrentPage()
            .selectOption(target.asSelector(), new SelectOption().setIndex(index));
        BrowseTheWebWithPlaywright.as(theUser).notifyScreenChange();
    }
}
