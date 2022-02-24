package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Step;

public class OpenUrl implements Interaction {

    private String url;
    public OpenUrl() {}
    public OpenUrl(String url) {
        this.url = url;
    }

    @Step("{0} opens the browser at #url")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWeb.as(theUser).openUrl(url);
    }
}
