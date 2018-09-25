package net.serenitybdd.screenplay.rest.interactions;

import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Step;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.rest.abiities.CallAnApi.as;

/**
 * An OPTIONS query
 * This is a simple interaction class suitable for simple queries.
 */
public class Patch extends RestInteraction {

    private final String resource;

    public Patch(String resource) {
        this.resource = resource;
    }

    @Step("{0} executes a PUT on the resource #resource")
    @Override
    public <T extends Actor> void performAs(T actor) {
        rest().options(as(actor).resolve(resource));
    }

    public static Patch to(String resource) {
        return instrumented(Patch.class, resource);
    }

}
