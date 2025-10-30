package net.serenitybdd.screenplay.rest.interactions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.rest.abilities.CallAnApi.as;

/**
 * Put something to a REST resource.
 * This is a simple interaction class suitable for simple queries.
 */
public class Delete extends RestInteraction {

    private final String resource;

    public Delete(String resource) {
        this.resource = resource;
    }

    @Step("{0} executes a DELETE on the resource #resource")
    @Override
    public <T extends Actor> void performAs(T actor) {
        rest().delete(as(actor).resolve(resource));
    }

    public static Delete from(String resource) {
        return instrumented(Delete.class, resource);
    }

}
