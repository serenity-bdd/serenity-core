package net.serenitybdd.screenplay;

import net.serenitybdd.screenplay.formatting.StripRedundantTerms;
import org.hamcrest.Matcher;

import static org.hamcrest.MatcherAssert.assertThat;

public class ValueConsequence<T> extends BaseConsequence<T> {
    private final T actual;
    private final Matcher<T> expected;

    public ValueConsequence(T actual, Matcher<T> expected) {
        this.actual = actual;
        this.expected = expected;
    }

    @Override
    public void evaluateFor(Actor actor) {
        if (thisStepShouldBeIgnored()) { return; }

        try {
            assertThat(actual, expected);
        } catch (Throwable actualError) {

            throwComplaintTypeErrorIfSpecified(errorFrom(actualError));

            throwDiagosticErrorIfProvided(errorFrom(actualError));

            throw actualError;
        }
    }

    private void throwDiagosticErrorIfProvided(Error actualError) {
        if (actual instanceof QuestionDiagnostics) {
            throw Complaint.from(((QuestionDiagnostics) actual).onError(), actualError);
        }
    }

    @Override
    public String toString() {
        String expectedExpression = StripRedundantTerms.from(expected.toString());
        return String.format("Then %s should be %s", actual, expectedExpression);
    }
}
