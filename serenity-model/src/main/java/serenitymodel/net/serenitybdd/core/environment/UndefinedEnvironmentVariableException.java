package serenitymodel.net.serenitybdd.core.environment;

import serenitymodel.net.serenitybdd.core.exceptions.TestCompromisedException;

public class UndefinedEnvironmentVariableException extends TestCompromisedException {
    public UndefinedEnvironmentVariableException(String message) {
        super(message);
    }
}
