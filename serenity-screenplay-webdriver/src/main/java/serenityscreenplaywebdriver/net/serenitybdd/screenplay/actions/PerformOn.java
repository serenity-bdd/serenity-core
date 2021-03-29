package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenitycore.net.serenitybdd.core.pages.WebElementFacade;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Performable;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.function.Consumer;

public class PerformOn implements Performable {
    private final Target target;
    private final Consumer<WebElementFacade> action;

    public PerformOn(Target target, Consumer<WebElementFacade> action) {
        this.target = target;
        this.action = action;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        target.resolveAllFor(actor).forEach(action);
    }

    public static Performable eachMatching(Target target, Consumer<WebElementFacade> action) {
        return new PerformOn(target, action);
    }

    public static Performable eachMatching(By byLocator, Consumer<WebElementFacade> action) {
        return eachMatching(Target.the(byLocator.toString()).located(byLocator), action);
    }

    public static Performable eachMatching(String locator, Consumer<WebElementFacade> action) {
        return eachMatching(Target.the(locator).locatedBy(locator), action);
    }
}
