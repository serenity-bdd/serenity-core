package net.serenitybdd.screenplay;

import net.serenitybdd.core.eventbus.Broadcaster;
import net.serenitybdd.screenplay.events.ActorAsksQuestion;
import net.serenitybdd.screenplay.formatting.StripRedundantTerms;
import org.hamcrest.Matcher;

import static org.hamcrest.MatcherAssert.assertThat;

public class QuestionConsequence<T> extends BaseConsequence<T> {
    private final Question<T> question;
    private final Matcher<T> expected;
    private final String subject;

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
        } catch (Throwable actualError) {

            throwComplaintTypeErrorIfSpecified(errorFrom(actualError));

            throwDiagosticErrorIfProvided(errorFrom(actualError));

            throw actualError;
        }
    }

    private void throwDiagosticErrorIfProvided(Error actualError) {
        if (question instanceof QuestionDiagnostics) {
            throw Complaint.from(((QuestionDiagnostics) question).onError(), actualError);
        }
    }

    @Override
    public String toString() {
        String expectedExpression = StripRedundantTerms.from(expected.toString());
        return String.format("Then %s should be %s", subject, expectedExpression);
    }
}
