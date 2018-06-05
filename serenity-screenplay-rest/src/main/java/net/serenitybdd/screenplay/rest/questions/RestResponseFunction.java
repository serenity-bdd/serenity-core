package net.serenitybdd.screenplay.rest.questions;

import io.restassured.response.Response;

import java.util.function.Function;

public interface RestResponseFunction<T> extends Function<Response, T> {}