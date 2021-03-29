package serenitymodel.net.serenitybdd.core.environment;

import serenitymodel.net.serenitybdd.core.exceptions.TestCompromisedException;

public class UnknownEnvironmentException extends TestCompromisedException {
    public UnknownEnvironmentException(String message) {
        super(message);
    }
}
