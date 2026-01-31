package net.serenitybdd.screenplay.playwright.interactions.selectactions;

import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

public class SelectByValueFromTarget implements Performable {
    private final Target target;
    private final String[] values;

    public SelectByValueFromTarget(Target target, String... values) {
        this.target = target;
        this.values = values;
    }

    @Step("{0} selects #values in #target")
    public <T extends Actor> void performAs(T theUser) {
        Page page = BrowseTheWebWithPlaywright.as(theUser).getCurrentPage();
        target.resolveFor(page).selectOption(values);
        BrowseTheWebWithPlaywright.as(theUser).notifyScreenChange();
    }
}
