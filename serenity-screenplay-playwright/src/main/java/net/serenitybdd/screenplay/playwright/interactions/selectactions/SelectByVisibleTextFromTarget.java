package net.serenitybdd.screenplay.playwright.interactions.selectactions;

import com.microsoft.playwright.options.SelectOption;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.thucydides.core.annotations.Step;

public class SelectByVisibleTextFromTarget implements Interaction {
    private final Target target;
    private final String option;

    public SelectByVisibleTextFromTarget(Target target, String option) {
        this.target = target;
        this.option = option;
    }

    @Step("{0} selects #option on #target")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWebWithPlaywright.as(theUser).getCurrentPage()
            .selectOption(target.asSelector(), new SelectOption().setLabel(option));
        BrowseTheWebWithPlaywright.as(theUser).notifyScreenChange();
    }
}
