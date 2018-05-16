package net.serenitybdd.screenplay.rest.questions;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.BaseConsequence;

import java.util.function.Consumer;

public class ResponseConsequence extends BaseConsequence<Response> {

    private final Consumer<ValidatableResponse> expression;

    ResponseConsequence(Consumer<ValidatableResponse> expression) {
        this.expression = expression;
    }

    @Override
    public void evaluateFor(Actor actor) {
        expression.accept(LastResponse.received().answeredBy(actor).then());
    }

    public static ResponseConsequence seeThatResponse(Consumer<ValidatableResponse> expression) {
        return new ResponseConsequence(expression);
    }
}
