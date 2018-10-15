package net.thucydides.core.annotations;

public class ManualTestMarkedAsError extends RuntimeException {
    public ManualTestMarkedAsError(String message) {
        super(message);
    }
}
