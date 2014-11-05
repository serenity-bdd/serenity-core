package net.thucydides.core.matchers;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;


public class PublicThucydidesMatchers {

    @Factory
    public static Matcher<TestOutcome> containsResults(TestResult... testResults) {
        return new TestOutcomeResultsMatcher(testResults);
    }
}
