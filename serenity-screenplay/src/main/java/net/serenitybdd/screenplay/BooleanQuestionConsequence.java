package net.serenitybdd.screenplay;

import net.serenitybdd.core.eventbus.Broadcaster;
import net.serenitybdd.screenplay.events.ActorAsksQuestion;
import net.thucydides.core.steps.StepEventBus;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BooleanQuestionConsequence<T> extends BaseConsequence<T> {
    private final Question<Boolean> question;
    private final String subject;

    public BooleanQuestionConsequence(Question<Boolean> actual) {
        this.question = actual;
        this.subject = QuestionSubject.fromClass(actual.getClass()).andQuestion(actual).subject();
    }

    @Override
    public void evaluateFor(Actor actor) {
        // TODO: Override if running consequences
        if (thisStepShouldBeIgnored() && !StepEventBus.getEventBus().softAssertsActive()) { return; }

        Broadcaster.postEvent(new ActorAsksQuestion(question));

        try {
            assertThat(reason(), question.answeredBy(actor), is(true));
        } catch (Throwable actualError) {

            throwComplaintTypeErrorIfSpecified(errorFrom(actualError));

            throwDiagosticErrorIfProvided(errorFrom(actualError));

            throw actualError;
        }
    }

    private String reason() {
        return "Expected " + QuestionSubject.fromClass(question.getClass());
    }

    private void throwDiagosticErrorIfProvided(Error actualError) {
        if (question instanceof QuestionDiagnostics) {
            throw Complaint.from(((QuestionDiagnostics) question).onError(), actualError);
        }
    }

    @Override
    public String toString() {
        return String.format("Then %s", subject);
    }
}
