package net.serenitybdd.screenplay.waits;


import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.questions.WebElementQuestion;
import net.serenitybdd.screenplay.targets.Target;
import org.hamcrest.Matcher;

import static net.serenitybdd.screenplay.EventualConsequence.eventually;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;

public class WaitUntilAngularIsReady implements Interaction {

    public WaitUntilAngularIsReady() {}

    @Override
    public <A extends Actor> void performAs(A actor) {
        BrowseTheWeb.as(actor).waitForAngularRequestsToFinish();
    }
}
