package net.serenitybdd.screenplay.rest.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.annotations.Step;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.rest.abilities.CallAnApi.as;

/**
 * Perform a GET query on a given REST resource
 */
public class Get extends RestInteraction{

    private final String resource;

    public Get(String resource) {
        this.resource = resource;
    }

    @Step("{0} executes a GET on the resource #resource")
    @Override
    public <T extends Actor> void performAs(T actor) {
        rest().get(as(actor).resolve(resource));
    }

    public static Get resource(String resource) {
        return instrumented(Get.class, resource);
    }
}
