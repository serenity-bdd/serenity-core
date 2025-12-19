package net.serenitybdd.cucumber.gherkin;

public class InvalidGherkinException extends RuntimeException{
    public InvalidGherkinException(String message) {
        super(message);
    }
}
