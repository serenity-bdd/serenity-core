package net.serenitybdd.core.environment;

import net.serenitybdd.core.exceptions.TestCompromisedException;

public class UnknownEnvironmentException extends TestCompromisedException {
    public UnknownEnvironmentException(String message) {
        super(message);
    }
}
