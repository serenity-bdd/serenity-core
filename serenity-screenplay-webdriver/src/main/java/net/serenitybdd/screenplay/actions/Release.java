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
public class Release {

    public static Performable mouse() {
        return Task.where("{0} release click",
                actor -> BrowseTheWeb.as(actor).withAction().release().build().perform()
        );
    }

    public static Performable on(String cssOrXpathForElement) {
        return Task.where("{0} release click",
                actor -> {
                    WebElement element = BrowseTheWeb.as(actor).find(cssOrXpathForElement);
                    BrowseTheWeb.as(actor).withAction().release(element).build().perform();
                }
        );
    }

    public static Performable on(Target target) {
        return Task.where("{0} release click " + target,
                actor -> {
                    WebElement element = target.resolveFor(actor);
                    BrowseTheWeb.as(actor).withAction().release(element).build().perform();
                }
        );
    }

    public static Performable on(WebElementFacade element) {
        return Task.where("{0} release click " + element,
                actor -> BrowseTheWeb.as(actor).withAction().release(element).build().perform()
        );
    }
}
