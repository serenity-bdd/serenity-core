package net.serenitybdd.junit5;

import org.opentest4j.TestAbortedException;

public class SkippedTestException extends TestAbortedException {

    public SkippedTestException(String message) {
        super(message);
    }

    @SuppressWarnings("unused")
    public SkippedTestException(String message, Throwable t) {
        super(message, t);
    }
}
