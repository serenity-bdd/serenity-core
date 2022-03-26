package net.serenitybdd.junit5;

import org.opentest4j.TestAbortedException;

public class PendingTestException extends TestAbortedException {

    public PendingTestException(String message) {
        super(message);
    }

    @SuppressWarnings("unused")
    public PendingTestException(String message, Throwable t) {
        super(message, t);
    }
}
