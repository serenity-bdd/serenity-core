package net.thucydides.model.steps;

public class FailingManualTestException extends RuntimeException {
    public FailingManualTestException(String message) {
        super(message);
    }
}
