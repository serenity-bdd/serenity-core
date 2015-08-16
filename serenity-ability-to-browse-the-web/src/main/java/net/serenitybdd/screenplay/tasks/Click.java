package net.serenitybdd.screenplay.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

public class Click implements Performable {

    private final String description;

    public static Performable on(String cssOrXpathForElement) {
        return new Click(cssOrXpathForElement);
    }

    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWeb.as(theUser).findBy(description)
                                .then().click();
    }

    public Click(String description) {
        this.description = description;
    }
}
