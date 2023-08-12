package net.serenitybdd.screenplay.actions;

import net.serenitybdd.model.exceptions.TestCompromisedException;

public class UnknownPageException extends TestCompromisedException {
    public UnknownPageException(String message) {
        super(message);
    }
}
