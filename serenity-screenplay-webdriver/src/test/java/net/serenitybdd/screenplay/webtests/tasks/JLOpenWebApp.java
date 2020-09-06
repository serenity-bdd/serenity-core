package net.serenitybdd.screenplay.webtests.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.*;
import net.serenitybdd.screenplay.webtests.pages.JLLoginPage;
import net.thucydides.core.annotations.Step;

public class JLOpenWebApp implements Performable {
    private JLLoginPage jlLoginPage;

    @Override
    @Step("{0} opens the application")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Open.browserOn().the(jlLoginPage));
    }
}
