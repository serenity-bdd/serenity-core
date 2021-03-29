package serenityscreenplayrest.net.serenitybdd.screenplay.rest.interactions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenitymodel.net.thucydides.core.annotations.Step;

import static serenityscreenplay.net.serenitybdd.screenplay.Tasks.instrumented;
import static serenityscreenplayrest.net.serenitybdd.screenplay.rest.abilities.CallAnApi.as;

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
