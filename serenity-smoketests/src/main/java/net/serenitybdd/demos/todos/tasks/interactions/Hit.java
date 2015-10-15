package net.serenitybdd.demos.todos.tasks.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.Keys;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Hit implements Performable {

    private Keys[] keys;
    private Target target;

    public static Hit the(Keys... keys) {
        Hit enterAction = instrumented(Hit.class);
        enterAction.keys = keys;
        return enterAction;
    }


    public Performable into(String cssOrXpathForElement) {
        this.target = Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement);
        return this;
    }

    public Performable into(Target target) {
        this.target = target;
        return this;
    }

    public Performable keyIn(String cssOrXpathForElement) { return into(cssOrXpathForElement); }
    public Performable keyIn(Target target) { return into(target); }

    @Step("{0} types '#keys' in #target")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWeb.as(theUser)
                .moveTo(target.getCssOrXPathSelector())
                .then().sendKeys(keys);
    }
}
