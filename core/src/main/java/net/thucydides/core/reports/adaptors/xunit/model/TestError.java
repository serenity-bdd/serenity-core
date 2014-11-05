package net.thucydides.core.reports.adaptors.xunit.model;

public class TestError extends Throwable {
    public TestError(String message) {
        super(message);
    }
}
