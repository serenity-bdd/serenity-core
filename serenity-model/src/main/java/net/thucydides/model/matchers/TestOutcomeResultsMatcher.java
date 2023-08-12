package net.thucydides.model.matchers;

import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.domain.TestStep;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Does a test outcome contain a given list of results, in the specified order?
 */
public class TestOutcomeResultsMatcher extends TypeSafeMatcher<TestOutcome> {

    private final List<TestResult> expectedTestResults;

    public TestOutcomeResultsMatcher(TestResult... expectedTestResults) {
        this.expectedTestResults = Arrays.asList(expectedTestResults);
    }

    @Override
    public boolean matchesSafely(TestOutcome testOutcome) {
        List<TestStep> allSteps = testOutcome.getFlattenedTestSteps();

        List<TestResult> allTestResults = allSteps.stream()
                .map(step -> step.getResult())
                .collect(Collectors.toList());

        return allTestResults.equals(expectedTestResults);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a test outcome with results " + Arrays.toString(expectedTestResults.toArray()));
    }
}

