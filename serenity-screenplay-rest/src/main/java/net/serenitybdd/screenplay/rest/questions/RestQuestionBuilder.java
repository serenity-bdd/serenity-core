package net.serenitybdd.screenplay.rest.questions;

import net.serenitybdd.screenplay.Question;

import java.util.ArrayList;
import java.util.List;

public class RestQuestionBuilder<T> implements To<T>, Returning<T> {

    private String name;
    private String endpoint;
    private List<RestQueryFunction> queries = new ArrayList<>();

    public To<T> about(String name) {
        this.name = name;
        return this;
    }

    public Returning<T> to(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public Returning<T> with(RestQueryFunction query) {
        this.queries.add(query);
        return this;
    }

    public Returning<T> withPathParameters(String param1, Object value1) {
        queries.add(request -> request.pathParam(param1, value1));
        return this;
    }

    public Returning<T> withPathParameters(String param1, Object value1,
                                           Object... otherParams) {

        this.queries.add(request -> request.pathParam(param1, value1));

        if (otherParams.length % 2 == 1) {
            throw new IllegalArgumentException("Invalid number of path parameters");
        }

        int paramIndex = 0;
        while (paramIndex < otherParams.length) {
            String param = otherParams[paramIndex].toString();
            Object value = otherParams[paramIndex + 1];
            paramIndex = paramIndex + 2;
            RestQueryFunction restQueryFunction = request -> request.pathParam(param, value);
            this.queries.add(restQueryFunction);
        }
        return this;
    }

    public Returning<T> withQueryParameters(String param1, Object value1) {
        queries.add(request -> request.queryParam(param1, value1));
        return this;
    }

    public Returning<T> withQueryParameters(String param1, Object value1,
                                            Object... otherParams) {

        this.queries.add(request -> request.queryParam(param1, value1));

        if (otherParams.length % 2 == 1) {
            throw new IllegalArgumentException("Invalid number of query parameters");
        }

        int paramIndex = 0;
        while (paramIndex < otherParams.length) {
            String param = otherParams[paramIndex].toString();
            Object value = otherParams[paramIndex + 1];
            paramIndex = paramIndex + 2;
            this.queries.add(request -> request.queryParam(param, value));
        }
        return this;
    }

    public Question<T> returning(RestResponseFunction<T> response) {
        return new RestQuestion<T>(name, endpoint, queries, response);
    }
}