package net.serenitybdd.screenplay;

import net.thucydides.core.steps.StepEventBus;
import org.hamcrest.Matcher;

import static org.hamcrest.MatcherAssert.assertThat;

public class ValueConsequence<T> implements Consequence<T> {
    private final T actual;
    private final Matcher<T> expected;
    private Class<? extends AssertionError> complaintType;

    public ValueConsequence(T actual, Matcher<T> expected) {
        this.actual = actual;
        this.expected = expected;
    }

    @Override
    public void evaluateFor(Actor actor) {
        if (thisStepShouldBeIgnored()) { return; }

        try {
            assertThat(actual, expected);
        } catch (AssertionError actualError) {

            throwComplaintTypeErrorIfSpecified(actualError);

            throwDiagosticErrorIfProvided(actualError);

            throw actualError;
        }
    }

    private void throwDiagosticErrorIfProvided(AssertionError actualError) {
        if (actual instanceof QuestionDiagnostics) {
            throw complaintFrom(((QuestionDiagnostics) actual).onError(), actualError);
        }
    }

    private void throwComplaintTypeErrorIfSpecified(AssertionError actualError) {
        if (complaintType != null) {
            throw complaintFrom(complaintType, actualError);
        }
    }

    private AssertionError complaintFrom(Class<? extends AssertionError> complaintType, AssertionError actualError) {
        try {
            return complaintType.getConstructor(Throwable.class).newInstance(actualError);
        } catch (Exception e) {
            return new AssertionError(String.format("%s should have a constructor that takes a nested exception",
                    complaintType.getSimpleName()));
        }
    }

    private boolean thisStepShouldBeIgnored() {
        return (StepEventBus.getEventBus().currentTestIsSuspended() || StepEventBus.getEventBus().aStepInTheCurrentTestHasFailed());
    }

    @Override
    public String toString() {
        return String.format("Then %s should be %s", actual, expected);
    }

    @Override
    public ValueConsequence<T> orComplainWith(Class<? extends AssertionError> complaintType) {
        this.complaintType = complaintType;
        return this;
    }
}
