package net.thucydides.model.matchers;

import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import org.hamcrest.Matcher;


public class PublicThucydidesMatchers {


    public static Matcher<TestOutcome> containsResults(TestResult... testResults) {
        return new TestOutcomeResultsMatcher(testResults);
    }
}
