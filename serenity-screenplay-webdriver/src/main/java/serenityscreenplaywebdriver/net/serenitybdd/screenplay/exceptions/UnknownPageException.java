package serenityscreenplaywebdriver.net.serenitybdd.screenplay.exceptions;

import serenitymodel.net.serenitybdd.core.exceptions.TestCompromisedException;

public class UnknownPageException extends TestCompromisedException {
    public UnknownPageException(String message) {
        super(message);
    }
}
