package serenityscreenplayrest.net.serenitybdd.screenplay.rest.questions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenityscreenplay.net.serenitybdd.screenplay.annotations.Subject;

@Subject("the response status")
public class TheResponseStatusCode implements Question<Integer> {

    @Override
    public Integer answeredBy(Actor actor) {
        return LastResponse.received().answeredBy(actor).statusCode();
    }
}
