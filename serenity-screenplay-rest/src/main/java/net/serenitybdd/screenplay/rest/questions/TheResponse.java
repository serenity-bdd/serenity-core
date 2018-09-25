package net.serenitybdd.screenplay.rest.questions;

import net.serenitybdd.screenplay.Question;

public class TheResponse {

    public static Question<Integer> statusCode() { return new TheResponseStatusCode(); }
}
