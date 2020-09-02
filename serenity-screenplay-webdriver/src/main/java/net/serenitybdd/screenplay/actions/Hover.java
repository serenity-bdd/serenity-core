package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import static net.serenitybdd.screenplay.Tasks.instrumented;

@Deprecated
/**
 * Deprecated - use MoveMouse instead
 */
public abstract class Hover implements Interaction {

    String target;

    public Hover() {
        this.target = getTarget();
    }

    public static Interaction over(Target target) {
        return instrumented(HoverOverTarget.class, target);
    }

    public static Interaction over(String cssOrXpathForElement) {
        return instrumented(HoverOverTarget.class, Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public static Interaction over(WebElement element) {
        return instrumented(HoverOverElement.class, element);
    }

    public static Interaction over(By... locators) {
        return instrumented(HoverOverBy.class, NewList.of(locators));
    }

    @Step("{0} hovers over #target")
    @Override
    public <T extends Actor> void performAs(T theActor) {
        as(theActor).moveToElement(resolveElementFor(theActor)).build().perform();
    }

    private Actions as(Actor theActor) {
        return new Actions(BrowseTheWeb.as(theActor).getDriver());
    }

    protected abstract WebElement resolveElementFor(Actor actor);

    protected abstract String getTarget();
}
