package net.thucydides.core.requirements.model.cucumber;

import net.serenitybdd.core.exceptions.TestCompromisedException;

public class InvalidFeatureFileException extends TestCompromisedException {
    public InvalidFeatureFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
