package serenityscreenplayrest.net.serenitybdd.screenplay.rest.interactions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenitymodel.net.thucydides.core.annotations.Step;

import static serenityscreenplay.net.serenitybdd.screenplay.Tasks.instrumented;
import static serenityscreenplayrest.net.serenitybdd.screenplay.rest.abilities.CallAnApi.as;

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
