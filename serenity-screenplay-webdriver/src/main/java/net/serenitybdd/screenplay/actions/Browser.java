package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

/**
 * Low level browser actions
 */
public class Browser {

    public static Performable refreshPage() {
        return Task.where("{0} refreshes the page",
                actor -> BrowseTheWeb.as(actor).getDriver().navigate().refresh()
        );
    }

    public static Performable deleteAllCookies() {
        return Task.where("{0} deletes browser cookies",
                actor -> BrowseTheWeb.as(actor).getDriver().manage().deleteAllCookies()
        );
    }

    public static Performable deleteCookieNamed(String cookieName) {
        return Task.where("{0} deletes browser cookies",
                actor -> BrowseTheWeb.as(actor).getDriver().manage().deleteCookieNamed(cookieName)
        );
    }

    public static Performable setSize(Dimension targetSize) {
        return Task.where("{0} deletes browser cookies",
                actor -> BrowseTheWeb.as(actor).getDriver().manage().window().setSize(targetSize)
        );
    }

    public static Performable setPosition(Point targetPosition) {
        return Task.where("{0} deletes browser cookies",
                actor -> BrowseTheWeb.as(actor).getDriver().manage().window().setPosition(targetPosition)
        );
    }

    public static Performable maximize() {
        return Task.where("{0} maximizes the browser",
                actor -> BrowseTheWeb.as(actor).getDriver().manage().window().maximize()
        );
    }

    public static Performable fullscreen() {
        return Task.where("{0} goes to full screen",
                actor -> BrowseTheWeb.as(actor).getDriver().manage().window().fullscreen()
        );
    }

    public static Performable navigateForward() {
        return Task.where("{0} navigates forward",
                actor -> BrowseTheWeb.as(actor).getDriver().navigate().forward()
        );
    }

    public static Performable navigateBack() {
        return Task.where("{0} navigates back",
                actor -> BrowseTheWeb.as(actor).getDriver().navigate().back()
        );
    }
}
