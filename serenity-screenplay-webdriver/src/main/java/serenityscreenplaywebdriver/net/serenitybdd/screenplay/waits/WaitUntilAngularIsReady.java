package serenityscreenplaywebdriver.net.serenitybdd.screenplay.waits;


import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.abilities.BrowseTheWeb;

public class WaitUntilAngularIsReady implements Interaction {

    public WaitUntilAngularIsReady() {}

    @Override
    public <A extends Actor> void performAs(A actor) {
        BrowseTheWeb.as(actor).waitForAngularRequestsToFinish();
    }
}
