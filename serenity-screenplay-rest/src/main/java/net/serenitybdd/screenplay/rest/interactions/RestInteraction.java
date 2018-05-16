package net.serenitybdd.screenplay.rest.interactions;

import io.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A base class for all Screenplay REST interactions, which gives direct access to the RestAssured API.
 */
public abstract class RestInteraction implements Interaction {

    Function<RequestSpecification, RequestSpecification> restConfiguration = requestSpecification -> requestSpecification;

    public RestInteraction with(Function<RequestSpecification, RequestSpecification> restConfiguration) {
        this.restConfiguration = restConfiguration;
        return this;
    }

    protected RequestSpecification rest() {
        return restConfiguration.apply(SerenityRest.given());
    }
}