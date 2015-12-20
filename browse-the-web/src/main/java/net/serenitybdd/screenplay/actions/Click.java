package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Action;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.core.targets.Target;
import net.thucydides.core.annotations.Step;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Click implements Action {

    private final Target target;

    public static Performable on(String cssOrXpathForElement) {
        return instrumented(Click.class, Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public static Performable on(Target target) {
        return instrumented(Click.class, target);
    }

    @Step("{0} clicks on #target")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWeb.as(theUser).findBy(target).then().click();
    }

    public Click(Target target) {
        this.target = target;
    }

}
