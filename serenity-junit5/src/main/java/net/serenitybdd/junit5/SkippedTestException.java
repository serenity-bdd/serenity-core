package net.serenitybdd.junit5;

import org.hamcrest.Matcher;
import org.junit.AssumptionViolatedException;

public class SkippedTestException extends AssumptionViolatedException {
    public <T> SkippedTestException(T actual, Matcher<T> matcher) {
        super(actual, matcher);
    }

    public <T> SkippedTestException(String message, T expected, Matcher<T> matcher) {
        super(message, expected, matcher);
    }

    public SkippedTestException(String message) {
        super(message);
    }

    public SkippedTestException(String message, Throwable t) {
        super(message, t);
    }
}
