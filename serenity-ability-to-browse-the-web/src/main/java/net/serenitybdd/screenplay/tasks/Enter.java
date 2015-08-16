package net.serenitybdd.screenplay.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Enter implements Performable {

    private String theText;
    private String description;

    public static Enter theValue(String text) {
        Enter enterAction = instrumented(Enter.class);
        enterAction.theText = text;
        return enterAction;
    }

    public Performable into(String cssOrXpathForElement) {
        this.description = cssOrXpathForElement;
        return this;
    }

    @Override
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWeb.as(theUser)
                .moveTo(description)
                .then().type(theText);
    }
}
