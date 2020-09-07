package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;

public interface Resolvable {
    WebElementFacade resolveFor(Actor actor);
}
