package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;
import serenitymodel.net.thucydides.core.annotations.Step;
import org.openqa.selenium.WebElement;

public class DoubleClickOnTarget implements Interaction {
    private final Target target;

    @Step("{0} clicks on #target")
    public <T extends Actor> void performAs(T theUser) {
        WebElement element = target.resolveFor(theUser);
        BrowseTheWeb.as(theUser).withAction().moveToElement(element).doubleClick().perform();
    }

    public DoubleClickOnTarget(Target target) {
        this.target = target;
    }

}
