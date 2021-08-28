package net.serenitybdd.screenplay.rest.questions;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.BaseConsequence;

import java.util.function.Consumer;

public class ResponseConsequence extends BaseConsequence<Response> {

    private final Consumer<ValidatableResponse> expression;
    private final String message;

    ResponseConsequence(String message,
                        Consumer<ValidatableResponse> expression) {
        this.message = message;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public void evaluateFor(Actor actor) {
        performSetupActionsAs(actor);
        expression.accept(LastResponse.received().answeredBy(actor).then());
    }

    public static ResponseConsequence seeThatResponse(String message, Consumer<ValidatableResponse> expression) {
        return new ResponseConsequence(message, expression);
    }

    public static ResponseConsequence seeThatResponse(Consumer<ValidatableResponse> expression) {
        return new ResponseConsequence("A valid response is returned", expression);
    }
}
