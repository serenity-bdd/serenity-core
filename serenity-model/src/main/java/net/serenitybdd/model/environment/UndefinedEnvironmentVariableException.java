package net.serenitybdd.model.environment;

import net.serenitybdd.model.exceptions.TestCompromisedException;

public class UndefinedEnvironmentVariableException extends TestCompromisedException {
    public UndefinedEnvironmentVariableException(String message) {
        super(message);
    }
}
