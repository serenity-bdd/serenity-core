package serenityscreenplayrest.net.serenitybdd.screenplay.rest.interactions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenitymodel.net.thucydides.core.annotations.Step;

import static serenityscreenplay.net.serenitybdd.screenplay.Tasks.instrumented;
import static serenityscreenplayrest.net.serenitybdd.screenplay.rest.abilities.CallAnApi.as;

/**
 * An OPTIONS query
 * This is a simple interaction class suitable for simple queries.
 */
public class Patch extends RestInteraction {

    private final String resource;

    public Patch(String resource) {
        this.resource = resource;
    }

    @Step("{0} executes a PATCH on the resource #resource")
    @Override
    public <T extends Actor> void performAs(T actor) {
        rest().patch(as(actor).resolve(resource));
    }

    public static Patch to(String resource) {
        return instrumented(Patch.class, resource);
    }

}
