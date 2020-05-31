package net.serenitybdd.screenplay.waits;


import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

public class WaitUntilAngularIsReady implements Interaction {

    public WaitUntilAngularIsReady() {}

    @Override
    public <A extends Actor> void performAs(A actor) {
        BrowseTheWeb.as(actor).waitForAngularRequestsToFinish();
    }
}
