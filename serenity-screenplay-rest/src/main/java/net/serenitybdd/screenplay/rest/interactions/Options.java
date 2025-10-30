package net.serenitybdd.screenplay.rest.interactions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.rest.abilities.CallAnApi.as;

/**
 * Path something to a REST resource.
 * This is a simple interaction class suitable for simple queries.
 */
public class Options extends RestInteraction {

    private final String resource;

    public Options(String resource) {
        this.resource = resource;
    }

    @Step("{0} executes a OPTIONS on the resource #resource")
    @Override
    public <T extends Actor> void performAs(T actor) {
        rest().options(as(actor).resolve(resource));
    }

    public static Options to(String resource) {
        return instrumented(Options.class, resource);
    }

}
