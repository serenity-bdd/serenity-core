package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.WebElement;

/**
 * Click on a button or element.
 */
public class ContextClick {

    public static Performable on(String cssOrXpathForElement) {
        return Task.where("{0} context-clicks on " + cssOrXpathForElement,
                actor -> {
                    WebElement element = BrowseTheWeb.as(actor).find(cssOrXpathForElement);
                    BrowseTheWeb.as(actor).withAction().contextClick(element).build().perform();
                }
        );
    }

    public static Performable on(Target target) {
        return Task.where("{0} context-clicks on " + target,
                actor -> {
                    WebElement element = target.resolveFor(actor);
                    BrowseTheWeb.as(actor).withAction().contextClick(element).build().perform();
                }
        );
    }

    public static Performable on(WebElementFacade element) {
        return Task.where("{0} context-clicks on " + element,
                actor -> {
                    BrowseTheWeb.as(actor).withAction().contextClick(element).build().perform();
                }
        );
    }
}
