package net.serenitybdd.screenplay.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.Keys;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Enter implements Performable {

    private String theText;
    private Target target;

    public static Enter theValue(String text) {
        Enter enterAction = instrumented(Enter.class);
        enterAction.theText = text;
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

    @Step("{0} enters '#theText' into #target")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWeb.as(theUser)
                .moveTo(target.getCssOrXPathSelector())
                .then().type(theText);
    }
}
