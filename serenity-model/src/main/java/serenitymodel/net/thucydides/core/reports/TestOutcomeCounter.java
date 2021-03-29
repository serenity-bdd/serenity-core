package serenitymodel.net.thucydides.core.reports;

import serenitymodel.net.thucydides.core.model.TestType;

/**
 * A basic builder used to count test outcomes of a particular type
 */
public class TestOutcomeCounter {

    protected final TestType testType;

    public TestOutcomeCounter(TestType testType) {
        this.testType = testType;
    }
}
