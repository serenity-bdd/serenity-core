package net.thucydides.core.steps;

import net.serenitybdd.model.collect.NewList;
import net.thucydides.model.steps.StepFailure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Result of a test step or sequence of test steps.
 */
public class TestResultTally implements Serializable {

    private static final long serialVersionUID = 1L;
    private final List<StepFailure> failures = new ArrayList<StepFailure>();
    private int ignored = 0;
    private int run = 0;
    final Class<?> classUnderTest;

    private TestResultTally(Class<?> classUnderTest) {
        this.classUnderTest = classUnderTest;
    }

    public Class<?> getClassUnderTest() {
        return classUnderTest;
    }

    /**
     * Record a test step failure.
     * Test step failures are recorded and reported at the end of the test case.
     */
    public void logFailure(final StepFailure failure) {
        failures.add(failure);
    }

    /**
     * A test step was ignored.
     */
    public void logIgnoredTest() {
        ignored++;
    }

    /**
     * A test step was executed.
     */
    public void logExecutedTest() {
        run++;
    }

    /**
     * How many test steps failed.
     */
    public int getFailureCount() {
        return failures.size();
    }

    /**
     * What were the failures.
     */
    public List<StepFailure> getFailures() {
        return NewList.copyOf(failures);
    }

    /**
     * How many test steps were ignored.
     */
    public int getIgnoreCount() {
        return ignored;
    }

    /**
     * How many test steps were executed, including ignored and failing test steps.
     */
    public int getRunCount() {
        return run;
    }

    /**
     * The test case is considered successful if there were no failing tests.
     */
    public boolean wasSuccessful() {
        return (getFailureCount() == 0);
    }

    public static TestResultTally forTestClass(final Class<?> classUnderTest) {
        return new TestResultTally(classUnderTest);
    }
}

