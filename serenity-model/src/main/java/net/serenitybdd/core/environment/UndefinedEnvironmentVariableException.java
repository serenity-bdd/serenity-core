package net.serenitybdd.core.environment;

import net.serenitybdd.core.exceptions.TestCompromisedException;

public class UndefinedEnvironmentVariableException extends TestCompromisedException {
    public UndefinedEnvironmentVariableException(String message) {
        super(message);
    }
}
