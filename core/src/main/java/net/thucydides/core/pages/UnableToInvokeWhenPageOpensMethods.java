package net.thucydides.core.pages;

/**
 * Could not invoke a Page Oject method annotated with the WhenPageOpens annotation.
 */
public class UnableToInvokeWhenPageOpensMethods extends RuntimeException {
    public UnableToInvokeWhenPageOpensMethods(String message, Throwable cause) {
        super(message,cause);
    }

    public UnableToInvokeWhenPageOpensMethods(String message) {
        super(message);
    }
}
