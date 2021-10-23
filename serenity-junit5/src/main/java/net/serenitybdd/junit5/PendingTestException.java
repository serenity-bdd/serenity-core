package net.serenitybdd.junit5;

import org.hamcrest.Matcher;
import org.junit.AssumptionViolatedException;

public class PendingTestException extends AssumptionViolatedException {
    public <T> PendingTestException(T actual, Matcher<T> matcher) {
        super(actual, matcher);
    }

    public <T> PendingTestException(String message, T expected, Matcher<T> matcher) {
        super(message, expected, matcher);
    }

    public PendingTestException(String message) {
        super(message);
    }

    public PendingTestException(String message, Throwable t) {
        super(message, t);
    }
}
