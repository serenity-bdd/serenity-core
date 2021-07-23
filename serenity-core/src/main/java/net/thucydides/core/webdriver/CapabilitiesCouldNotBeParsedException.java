package net.thucydides.core.webdriver;

public class CapabilitiesCouldNotBeParsedException extends RuntimeException {
    public CapabilitiesCouldNotBeParsedException(String message, Throwable exception) {
        super(message, exception);
    }
}
