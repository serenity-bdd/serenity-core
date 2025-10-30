package net.serenitybdd.screenplay.actions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.WebElement;

public class RightClickOnTarget implements Interaction {
    private final Target target;

    @Step("{0} right-clicks on #target")
    public <T extends Actor> void performAs(T theUser) {
        WebElement element = target.resolveFor(theUser);
        BrowseTheWeb.as(theUser).withAction().contextClick(element).perform();
    }

    public RightClickOnTarget(Target target) {
        this.target = target;
    }
}
