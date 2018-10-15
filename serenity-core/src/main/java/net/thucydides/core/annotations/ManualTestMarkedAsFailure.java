package net.thucydides.core.annotations;

public class ManualTestMarkedAsFailure extends AssertionError {
    public ManualTestMarkedAsFailure(String message) {
        super(message);
    }
}
