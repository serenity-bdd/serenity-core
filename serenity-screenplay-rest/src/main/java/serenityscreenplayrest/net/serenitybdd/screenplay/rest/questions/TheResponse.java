package serenityscreenplayrest.net.serenitybdd.screenplay.rest.questions;

import serenityscreenplay.net.serenitybdd.screenplay.Question;

public class TheResponse {

    public static Question<Integer> statusCode() { return new TheResponseStatusCode(); }
}
