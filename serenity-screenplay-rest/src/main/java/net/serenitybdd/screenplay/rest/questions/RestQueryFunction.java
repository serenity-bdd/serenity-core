package net.serenitybdd.screenplay.rest.questions;

import io.restassured.specification.RequestSpecification;

import java.util.function.Function;

public interface RestQueryFunction extends Function<RequestSpecification,RequestSpecification> {}