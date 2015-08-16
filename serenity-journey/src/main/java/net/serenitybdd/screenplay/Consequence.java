package net.serenitybdd.screenplay;

import org.hamcrest.Matcher;

import static org.hamcrest.MatcherAssert.assertThat;

public class Consequence<T> {
    private final Question<T> actual;
    private final Matcher<T> expected;
    private final String subject;

    protected Consequence(Question<T> actual, Matcher<T> expected) {
        this.actual = actual;
        this.expected = expected;
        this.subject = QuestionSubject.fromClass(actual.getClass()).andQuestion(actual).subject();
    }

    public void evaluateFor(Actor actor) {
        assertThat(actual.answeredBy(actor), expected);
    }

    @Override
    public String toString() {
        return String.format("Then %s should be %s", subject, expected);
    }
}
