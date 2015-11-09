package net.serenitybdd.screenplay;

import net.serenitybdd.screenplay.exceptions.IgnoreStepException;
import net.thucydides.core.steps.StepEventBus;
import org.hamcrest.Matcher;

import static org.hamcrest.MatcherAssert.assertThat;

public class Consequence<T> {
    private final Question<T> actual;
    private final Matcher<T> expected;
    private final String subject;

    public Consequence(Question<T> actual, Matcher<T> expected) {
        this.actual = actual;
        this.expected = expected;
        this.subject = QuestionSubject.fromClass(actual.getClass()).andQuestion(actual).subject();
    }

    public void evaluateFor(Actor actor) {
        ensureThisStepShouldNotBeIgnored();
        assertThat(actual.answeredBy(actor), expected);
    }

    private void ensureThisStepShouldNotBeIgnored() {
        if (StepEventBus.getEventBus().currentTestIsSuspended() || StepEventBus.getEventBus().aStepInTheCurrentTestHasFailed()) {
            throw new IgnoreStepException();
        }
    }

    @Override
    public String toString() {
        return String.format("Then %s should be %s", subject, expected);
    }
}
