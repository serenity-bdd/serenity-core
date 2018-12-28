package net.thucydides.core.steps;

public class FailingManualTestException extends RuntimeException {
    public FailingManualTestException(String message) {
        super(message);
    }
}
