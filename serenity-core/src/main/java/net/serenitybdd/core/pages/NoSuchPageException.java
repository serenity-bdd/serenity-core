package net.serenitybdd.core.pages;

import net.serenitybdd.model.exceptions.TestCompromisedException;

public class NoSuchPageException  extends TestCompromisedException {
    public NoSuchPageException(String message) {
        super(message);
    }
}
