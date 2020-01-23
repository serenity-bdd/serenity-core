package net.serenitybdd.core.webdriver;

public class UnexpectedDriverExpected extends RuntimeException {
    public UnexpectedDriverExpected(String message) {
        super(message);
    }
}
