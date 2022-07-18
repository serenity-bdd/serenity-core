package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class DragInteraction {
    private final Target from;

    public DragInteraction(Target from) {
        this.from = from;
    }

    public Performable to(Target to) {
        return Task.where("{0} drags the " + from + " element to " + to,
                actor -> {
                    WebElement fromElemennt = from.resolveFor(actor);
                    WebElement toElemennt = to.resolveFor(actor);
                    BrowseTheWeb.as(actor).withAction().dragAndDrop(fromElemennt, toElemennt).build().perform();
                }
        );
    }

    public Performable to(String xpathOrCssLocator) {
        return Task.where("{0} drags the " + from + " element to " + xpathOrCssLocator,
                actor -> {
                    WebElement fromElemennt = from.resolveFor(actor);
                    WebElement toElemennt = BrowseTheWeb.as(actor).find(xpathOrCssLocator);
                    BrowseTheWeb.as(actor).withAction().dragAndDrop(fromElemennt, toElemennt).build().perform();
                }
        );
    }

    public Performable to(By locator) {
        return Task.where("{0} drags the " + from + " element to " + locator,
                actor -> {
                    WebElement fromElemennt = from.resolveFor(actor);
                    WebElement toElemennt = BrowseTheWeb.as(actor).find(locator);
                    BrowseTheWeb.as(actor).withAction().dragAndDrop(fromElemennt, toElemennt).build().perform();
                }
        );
    }
}
