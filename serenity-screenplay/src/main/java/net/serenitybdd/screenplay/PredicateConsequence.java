package net.serenitybdd.screenplay;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.eventbus.Broadcaster;
import net.serenitybdd.screenplay.conditions.SilentPerformable;
import net.serenitybdd.screenplay.events.ActorAsksQuestion;
import net.serenitybdd.screenplay.formatting.StripRedundantTerms;
import net.thucydides.core.steps.StepEventBus;
import org.hamcrest.Matcher;

import java.util.Optional;
import java.util.function.Predicate;

import static net.serenitybdd.screenplay.questions.QuestionHints.addHints;
import static net.serenitybdd.screenplay.questions.QuestionHints.fromAssertion;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class PredicateConsequence<T> extends BaseConsequence<T> {
    protected final Question<T> question;
    protected final Predicate<T> expected;
    protected final String subject;

    private final static SilentPerformable DO_NOTHING = new SilentPerformable();

    public PredicateConsequence(Question<T> actual, Predicate<T> expected) {
        this(null, actual, expected);
    }

    public PredicateConsequence(String subjectText, Question<T> actual, Predicate<T> expected) {
        this.question = actual;
        this.expected = expected;
        this.subject = QuestionSubject.fromClass(actual.getClass()).andQuestion(actual).subject();
        this.subjectText = Optional.ofNullable(subjectText);
    }

    @Override
    public void evaluateFor(Actor actor) {
        if (thisStepShouldBeIgnored() && !StepEventBus.getEventBus().softAssertsActive()) { return; }

        Broadcaster.getEventBus().post(new ActorAsksQuestion(question, actor.getName()));

        Serenity.injectScenarioStepsInto(question);

        try {
            optionalPrecondition.orElse(DO_NOTHING).performAs(actor);

            assertTrue("predicate failed", expected.test(question.answeredBy(actor)));

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
        String template = explanation.orElse("Then %s should be %s");
        String expectedExpression = StripRedundantTerms.from(expected.toString());
        return addRecordedInputValuesTo(String.format(template, subjectText.orElse(subject), expectedExpression));
    }
}
