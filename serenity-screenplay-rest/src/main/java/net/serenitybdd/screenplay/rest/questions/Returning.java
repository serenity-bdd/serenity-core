package net.serenitybdd.screenplay.rest.questions;

import net.serenitybdd.screenplay.Question;

public interface Returning<T> {
    Returning<T> with(RestQueryFunction query);

    Returning<T> withPathParameters(String param1, Object value1);

    Returning<T> withPathParameters(String param1, Object value1,
                                    Object... otherParams);

    Returning<T> withQueryParameters(String param1, Object value1);

    Returning<T> withQueryParameters(String param1, Object value1,
                                     Object... otherParams);

    Question<T> returning(RestResponseFunction<T> response);
}