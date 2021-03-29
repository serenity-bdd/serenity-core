package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenitycore.net.serenitybdd.core.pages.WebElementFacade;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;

public interface Resolvable {
    WebElementFacade resolveFor(Actor actor);
}
