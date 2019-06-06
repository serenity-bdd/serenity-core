package net.serenitybdd.screenplay.rest.interactions;

import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Step;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.rest.abilities.CallAnApi.as;

/**
 * Path something to a REST resource.
 * This is a simple interaction class suitable for simple queries.
 */
public class Head extends RestInteraction {

    private final String resource;

    public Head(String resource) {
        this.resource = resource;
    }

    @Step("{0} executes a HEAD on the resource #resource")
    @Override
    public <T extends Actor> void performAs(T actor) {
        rest().head(as(actor).resolve(resource));
    }

    public static Head to(String resource) {
        return instrumented(Head.class, resource);
    }

}
