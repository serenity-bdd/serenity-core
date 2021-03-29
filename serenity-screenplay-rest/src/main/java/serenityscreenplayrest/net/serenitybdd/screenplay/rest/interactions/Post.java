package serenityscreenplayrest.net.serenitybdd.screenplay.rest.interactions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenitymodel.net.thucydides.core.annotations.Step;

import static serenityscreenplay.net.serenitybdd.screenplay.Tasks.instrumented;
import static serenityscreenplayrest.net.serenitybdd.screenplay.rest.abilities.CallAnApi.as;

/**
 * Post something to a REST resource.
 * This is a simple interaction class suitable for simple queries.
 */
public class Post extends RestInteraction {

    private final String resource;

    public Post(String resource) {
        this.resource = resource;
    }

    @Step("{0} executes a POST on the resource #resource")
    @Override
    public <T extends Actor> void performAs(T actor) {
        rest().post(as(actor).resolve(resource));
    }

    public static Post to(String resource) {
        return instrumented(Post.class, resource);
    }

}
