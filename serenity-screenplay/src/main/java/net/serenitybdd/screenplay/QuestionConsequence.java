package net.serenitybdd.screenplay;

import com.google.common.base.Optional;
import net.serenitybdd.core.eventbus.Broadcaster;
import net.serenitybdd.screenplay.conditions.SilentPerformable;
import net.serenitybdd.screenplay.events.ActorAsksQuestion;
import net.serenitybdd.screenplay.formatting.StripRedundantTerms;
import net.thucydides.core.steps.StepEventBus;
import org.hamcrest.Matcher;

import static org.hamcrest.MatcherAssert.assertThat;

public class QuestionConsequence<T> extends BaseConsequence<T> {
    protected final Question<T> question;
    protected final Matcher<T> expected;
    protected final String subject;

    private final static SilentPerformable DO_NOTHING = new SilentPerformable();

    public QuestionConsequence(Question<T> actual, Matcher<T> expected) {
        this(null, actual, expected);
    }

    public QuestionConsequence(String subjectText, Question<T> actual, Matcher<T> expected) {
        this.question = actual;
        this.expected = expected;
        this.subject = QuestionSubject.fromClass(actual.getClass()).andQuestion(actual).subject();
        this.subjectText = Optional.fromNullable(subjectText);
    }

    @Override
    public void evaluateFor(Actor actor) {
        if (thisStepShouldBeIgnored() && !StepEventBus.getEventBus().softAssertsActive()) { return; }

        Broadcaster.getEventBus().post(new ActorAsksQuestion(question));

        try {
            optionalPrecondition.or(DO_NOTHING).performAs(actor);
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
        String template = explanation.or("Then %s should be %s");
        String expectedExpression = StripRedundantTerms.from(expected.toString());
        return addRecordedInputValuesTo(String.format(template, subjectText.or(subject), expectedExpression));
    }
}
