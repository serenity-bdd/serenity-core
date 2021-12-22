package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.thucydides.core.annotations.Step;

public class OpenUrl implements Interaction {
    String url;

    public OpenUrl(String url) {
        this.url = url;
    }

    @Override
    @Step("{0} opens the URL #url")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright.as(actor).getCurrentPage().navigate(url);
        BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
    }
}
