package net.serenitybdd.core.pages;

import net.serenitybdd.core.exceptions.TestCompromisedException;

public class NoSuchPageException  extends TestCompromisedException {
    public NoSuchPageException(String message) {
        super(message);
    }
}
