package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Hover implements Interaction {

    public static Hover over(Target target) {
        return instrumented(Hover.class, target);
    }

    private final Target target;

    public Hover(Target target) {
        this.target = target;
    }

    @Step("{0} hovers over #target")
    @Override
    public <T extends Actor> void performAs(T theActor) {
        WebElement theElement = target.resolveFor(theActor);

        as(theActor).moveToElement(theElement).build().perform();
    }

    private Actions as(Actor theActor) {
        return new Actions(BrowseTheWeb.as(theActor).getDriver());
    }
}
