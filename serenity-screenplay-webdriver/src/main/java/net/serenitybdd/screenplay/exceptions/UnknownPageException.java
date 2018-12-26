package net.serenitybdd.screenplay.exceptions;

import net.serenitybdd.core.exceptions.TestCompromisedException;

public class UnknownPageException extends TestCompromisedException {
    public UnknownPageException(String message) {
        super(message);
    }
}
