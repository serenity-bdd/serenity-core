package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import serenitymodel.net.thucydides.core.annotations.Step;

public class OpenAt implements Interaction {

    private final String url;

    public OpenAt(String url) {
        this.url = url;
    }

    @Step("{0} opens the #url")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWeb.as(theUser).openAt(url);
    }


}
