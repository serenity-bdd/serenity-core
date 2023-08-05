package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.annotations.Step;

public class OpenAt implements Interaction {

    private String url;

    public OpenAt(){}

    public OpenAt(String url) {
        this.url = url;
    }

    @Step("{0} opens the #url")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWeb.as(theUser).openAt(url);
    }


}
