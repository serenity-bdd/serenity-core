package serenityscreenplayrest.net.serenitybdd.screenplay.rest.questions;

import io.restassured.response.Response;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenityscreenplay.net.serenitybdd.screenplay.annotations.Subject;
import serenityscreenplayrest.net.serenitybdd.screenplay.rest.abilities.CallAnApi;

@Subject("the response received")
public class LastResponse implements Question<Response> {

    @Override
    public Response answeredBy(Actor actor) {
        return CallAnApi.as(actor).getLastResponse();
    }

    public static LastResponse received() {
        return new LastResponse();
    }
}
