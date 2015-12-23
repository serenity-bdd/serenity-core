package net.serenitybdd.screenplay;

import net.serenitybdd.core.eventbus.Broadcaster;
import net.serenitybdd.screenplay.events.ActorAsksQuestion;
import net.thucydides.core.steps.StepEventBus;
import org.hamcrest.Matcher;

import static org.hamcrest.MatcherAssert.assertThat;

public class QuestionConsequence<T> implements Consequence<T> {
    private final Question<T> question;
    private final Matcher<T> expected;
    private final String subject;
    private Class<? extends AssertionError> complaintType;

    public QuestionConsequence(Question<T> actual, Matcher<T> expected) {
        this.question = actual;
        this.expected = expected;
        this.subject = QuestionSubject.fromClass(actual.getClass()).andQuestion(actual).subject();
    }

    @Override
    public void evaluateFor(Actor actor) {
        if (thisStepShouldBeIgnored()) { return; }

        Broadcaster.getEventBus().post(new ActorAsksQuestion(question));
        try {
            assertThat(question.answeredBy(actor), expected);
        } catch (AssertionError actualError) {

            throwComplaintTypeErrorIfSpecified(actualError);

            throwDiagosticErrorIfProvided(actualError);

            throw actualError;
        }
    }

    private void throwDiagosticErrorIfProvided(AssertionError actualError) {
        if (question instanceof QuestionDiagnostics) {
            throw complaintFrom(((QuestionDiagnostics) question).onError(), actualError);
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
        return String.format("Then %s should be %s", subject, expected);
    }

    @Override
    public QuestionConsequence<T> orComplainWith(Class<? extends AssertionError> complaintType) {
        this.complaintType = complaintType;
        return this;
    }
}
