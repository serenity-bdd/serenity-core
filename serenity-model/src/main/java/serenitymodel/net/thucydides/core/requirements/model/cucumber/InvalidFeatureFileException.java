package serenitymodel.net.thucydides.core.requirements.model.cucumber;

import serenitymodel.net.serenitybdd.core.exceptions.TestCompromisedException;

public class InvalidFeatureFileException extends TestCompromisedException {
    public InvalidFeatureFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
