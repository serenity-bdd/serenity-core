package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.thucydides.core.annotations.Step;

/**
 * This method double clicks an element matching selector.
 * More info at: https://playwright.dev/java/docs/api/class-page#pagedblclickselector-options
 */
public class DoubleClick implements Performable {

    private final Target target;
    private Page.DblclickOptions options;

    public DoubleClick(Target target) {
        this.target = target;
    }

    public static DoubleClick on(String selector) {
        return new DoubleClick(Target.the(selector).locatedBy(selector));
    }

    public static DoubleClick on(Target target) {
        return new DoubleClick(target);
    }

    public Performable withOptions(Page.DblclickOptions options) {
        this.options = options;
        return this;
    }

    @Override
    @Step("{0} double clicks on #target")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright.as(actor).getCurrentPage().dblclick(target.asSelector(), options);
        BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
    }
}
