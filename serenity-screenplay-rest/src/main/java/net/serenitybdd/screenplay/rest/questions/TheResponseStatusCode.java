package net.serenitybdd.screenplay.rest.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class TheResponseStatusCode implements Question<Integer> {

    @Override
    public Integer answeredBy(Actor actor) {
        return LastResponse.received().answeredBy(actor).statusCode();
    }
}
