package net.serenitybdd.screenplay.playwright.interactions.selectactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.thucydides.core.annotations.Step;

public class SelectByVisibleTextFromTarget implements Interaction {
    private final Target target;
    private final String[] options;
    private final String selectedOptions;

    public SelectByVisibleTextFromTarget(Target target, String... options) {
        this.target = target;
        this.options = options;
        this.selectedOptions = String.join(",", options);
    }

    @Step("{0} selects #selectedOptions on #target")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWebWithPlaywright.as(theUser).getCurrentPage().selectOption(target.asSelector(), options);
        BrowseTheWebWithPlaywright.as(theUser).notifyScreenChange();
    }
}
