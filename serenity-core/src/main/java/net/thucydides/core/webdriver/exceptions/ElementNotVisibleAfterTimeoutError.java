package net.thucydides.core.webdriver.exceptions;

import net.serenitybdd.model.exceptions.CausesAssertionFailure;

public class ElementNotVisibleAfterTimeoutError extends Error implements CausesAssertionFailure {
    public ElementNotVisibleAfterTimeoutError(String message) {
        super(message);
    }
}
