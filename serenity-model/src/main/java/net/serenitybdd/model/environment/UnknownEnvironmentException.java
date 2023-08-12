package net.serenitybdd.model.environment;

import net.serenitybdd.model.exceptions.TestCompromisedException;

public class UnknownEnvironmentException extends TestCompromisedException {
    public UnknownEnvironmentException(String message) {
        super(message);
    }
}
