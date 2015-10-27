package net.thucydides.core.webdriver.exceptions;

public class ElementNotVisibleAfterTimeoutError extends Error implements CausesAssertionFailure {
    public ElementNotVisibleAfterTimeoutError(String message) {
        super(message);
    }
}