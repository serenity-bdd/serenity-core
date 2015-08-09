package net.serenitybdd.screenplay;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matcher;

import static org.apache.commons.lang3.StringUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class Consequence<T> {
    private final T actual;
    private final Matcher<T> expected;
    private String subject;

    public Consequence(T actual, Matcher<T> expected) {
        this(actual,expected,"");
    }

    protected Consequence(T actual, Matcher<T> expected, String subject) {
        this.actual = actual;
        this.expected = expected;
        this.subject = subject;
    }

    public void evaluate() {
        assertThat(actual, expected);
    }

    @Override
    public String toString() {
        return (expected.matches(actual)) ? shortDescription() : shortDescription() + " but is " + actual;
    }

    public Consequence<T> about(String subject) {
        return new Consequence<T>(actual, expected, subject);
    }

    public String shortDescription() {
        return String.format("Then %s should be %s", subject, expected);
    }
}
