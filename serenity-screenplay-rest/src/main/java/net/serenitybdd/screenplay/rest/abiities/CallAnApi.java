package net.serenitybdd.screenplay.rest.abiities;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;

import java.util.function.Function;

/**
 * A Screenplay ability that allows an actor to perform REST queries against a specified API.
 * For example:
 * ```
 * Actor sam = Actor.named("Sam the supervisor").whoCan(CallAnApi.at("https://reqres.in"));
 * ```
 */
public class CallAnApi implements Ability {

    private final String baseURL;

    private Response lastResponse;

    private CallAnApi(String baseURL) {
        this.baseURL = baseURL;
    }

    /**
     * Ability to Call and api at a specified baseUrl
     */
    public static CallAnApi at(String baseURL) {
        return new CallAnApi(baseURL);
    }

    /**
     * Used to access the Actor's ability to CallAnApi from within the Interaction classes, such as GET or PUT
     */
    public static CallAnApi as(Actor actor) {
        return actor.abilityTo(CallAnApi.class);
    }

    /**
     * Send a GET request to a specified url.
     * Response will be resolved and made available as lastResponse.
     *
     */
    public void get(String resource) {
        lastResponse = SerenityRest.given().get(baseURL + resource);
    }

    public String resolve(String resource) {
        return baseURL + resource;
    }

    Function<RequestSpecification, RequestSpecification> config = request -> request.header("Content-Type", "application/json")
            .body("{\"name\": \"joe\",\"job\": \"leader\"}");

    public void post(String resource) {
        RequestSpecification s = config.apply(SerenityRest.given());

        s.post(baseURL + resource);
//        lastResponse = SerenityRest.given().post(baseURL + resource);
    }

    public Response getLastResponse() {
        return SerenityRest.lastResponse();
    }

    public RequestSpecification withRestAssured() {
        return SerenityRest.rest();
    }
}
