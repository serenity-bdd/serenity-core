package net.serenitybdd.screenplay.rest.questions;

import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.rest.interactions.Get;

import java.util.List;
import java.util.function.Function;

/**
 * A REST query that sends a GET request to an endpoint and returns a result of a given type.
 * Sample usage:
 * public static Question&lt;Float&gt; cashBalanceFor(Client client) {
 *         return new RestQuestionBuilder&lt;Float&gt;().about("Cash account balance")
 *                                                .to("/client/{clientId}/portfolio")
 *                                                .withParameters(request -> request.pathParam("clientId", client.getId()))
 *                                                .returning(response -> response.path("cash"));
 * }
 */
@Subject("#name")
public class RestQuestion<T> implements Question<T> {

    private final List<RestQueryFunction> queries;
    private final String endpoint;
    private final String name;
    private final Function<Response, T> result;

    public RestQuestion(String name,
                        String endpoint,
                        List<RestQueryFunction> queries,
                        Function<Response, T> result) {
        this.name = name;
        this.endpoint = endpoint;
        this.queries = queries;
        this.result = result;
    }


    @Override
    public String toString() {
        return name;
    }

    @Override
    public T answeredBy(Actor actor) {

        actor.attemptsTo(
                Get.resource(endpoint).with(queries)
        );

        return result.apply(SerenityRest.lastResponse());
    }
}
