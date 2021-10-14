package net.thucydides.core.webdriver.exceptions;

public class ElementNotFoundAfterTimeoutError extends Error {
    public ElementNotFoundAfterTimeoutError(String message) {
        super(message);
    }
    public ElementNotFoundAfterTimeoutError(String message, Throwable throwable) {
        super(message, throwable);
    }
}