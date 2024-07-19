package net.thucydides.model.domain;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Acceptance test results. 
 * Records the possible outcomes of tests within an acceptance test case
 * and of the overall acceptance test case itself.
 * 
 * @author johnsmart
 */
public enum TestResult {
    /**
     * Test result not known yet.
     */
    UNDEFINED(0, false, "Undefined"),
    /**
     * The test or test case ran as expected.
     */
    SUCCESS(1, true, "Passing"),
    /**
     * A pending test is one that has been specified but not yet implemented.
     * In a JUnit test case, you can use the (Thucydides) @Pending annotation to mark this.
     * A pending test case is a test case that has at least one pending test.
     */
    PENDING(2, false, "Pending"),
    /**
     * The test or test case was deliberately ignored.
     * Tests can be ignored via the @Ignore annotation in JUnit, for example.
     * Ignored tests are not considered the same as pending tests: a pending test is one that
     * has been specified, but the corresponding code is yet to be implemented, whereas an
     * ignored test can be a temporarily-deactivated test (during refactoring, for example).
     */
    IGNORED(2, false, "Ignored"),
    /**
     * The test step was not executed because a previous step in this test case failed.
     * A whole test case can be skipped using tags or annotations to indicate that it is currently "work-in-progress"
     */
    SKIPPED(2, false, "Skipped"),
    /**
     * Test is skipped due to a failing assumption
     */
    ABORTED(2, false, "Aborted"),
    /**
     * Test failure, due to an assertion error
     * For a test case, this means one of the tests in the test case failed.
     */
    FAILURE(3, true, "Failing"),
    /**
     * Test failure, due to some other exception.
     */
    ERROR(4, true, "Broken"),
    /**
     * Test failures due to external events or systems that compromise the validity of the test.
     */
    COMPROMISED(5, true, "Compromised"),
    /**
     * Either failure, error or compromised - internal use only
     */
    UNSUCCESSFUL(6, true, "Unsuccessful");

    private final int priority;
    private final boolean executedResultsCount;
    private final String adjective;

    private static Logger logger = LoggerFactory.getLogger("net.thucydides.model.model.TestResult");

    TestResult(int priority, boolean executedResultsCount, String adjective) {
        this.priority = priority;
        this.executedResultsCount = executedResultsCount;
        this.adjective = adjective;
    }

    public int getPriority() {
        return priority;
    }

    public boolean overrides(TestResult result) {
        return priority > result.priority;
    }

    public boolean isMoreSevereThan(TestResult otherResult) {
        return ordinal() > otherResult.ordinal();
    }

    public boolean isLessSevereThan(TestResult otherResult) {
        return ordinal() < otherResult.ordinal();
    }

    public String getLabel() {
        return adjective + " tests";
    }

    public static boolean existsWithName(String expectedResult) {
        try {
            valueOf(expectedResult);
        } catch (IllegalArgumentException noMatchingEnumValue) {
            logger.warn("No matching test result value found for {}", expectedResult);
            return false;
        }
        return true;
    }

    public boolean isAtLeast(TestResult minimumTestResult) {
        return this.priority >= minimumTestResult.priority;
    }
    public boolean executedResultsCount() {
        return executedResultsCount;
    }

    public String getAdjective() {
        return adjective;
    }

    public boolean isUnsuccessful() {
        return (this == FAILURE || this == ERROR || this == COMPROMISED);
    }

    public boolean isEqualTo(String name) {
        return this.name().equalsIgnoreCase(name) || this.adjective.equalsIgnoreCase(name);
    }

    private static final Map<TestResult, Set<TestResult>> AS_SETS = new HashMap<>();
    static {
        for(TestResult value : values()) {
            Set<TestResult> resultAsSet = new HashSet<>();
            resultAsSet.add(value);
            AS_SETS.put(value, resultAsSet);
        }

        Set<TestResult> unsuccessful = new HashSet<>();
        unsuccessful.add(FAILURE);
        unsuccessful.add(ERROR);
        unsuccessful.add(COMPROMISED);
        AS_SETS.put(UNSUCCESSFUL, unsuccessful);
    }

    public Set<TestResult> expanded() {
        return AS_SETS.get(this);
    }
}
