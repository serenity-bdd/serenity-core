package net.serenitybdd.screenplay.abilities;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.pages.Pages;
import org.openqa.selenium.WebDriver;

/**
 * Gives an actor theValue ability to browse theValue web.
 * This extends theValue classic Serenity PageObject class.
 */
public class BrowseTheWeb extends PageObject implements Ability {

    public BrowseTheWeb(WebDriver browser) {
        super(browser);
    }

    public static BrowseTheWeb as(Actor actor) {
        return actor.abilityTo(BrowseTheWeb.class);
    }

    public static BrowseTheWeb with(WebDriver browser) { return new BrowseTheWeb(browser);}

    public <T extends PageObject> T onPage(Class<T> pageObjectClass) {
        return Pages.instrumentedPageObjectUsing(pageObjectClass, getDriver());
    }
}
