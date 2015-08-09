package net.thucydides.core.model;


/**
 * Acceptance test results. 
 * Records the possible outcomes of tests within an acceptance test case
 * and of the overall acceptance test case itself.
 * 
 * @author johnsmart
 *
 */
public enum TestResult {
    /**
     *  Test failure, due to some other exception.
     */
     ERROR(4),
    /**
     * Test failure, due to an assertion error
     * For a test case, this means one of the tests in the test case failed.
     */
    FAILURE(3),

    /**
     * The test step was not executed because a previous step in this test case failed.
     * A whole test case can be skipped using tags or annotations to indicate that it is currently "work-in-progress"
     */
    SKIPPED(2),
    /**
     * The test or test case was deliberately ignored.
     * Tests can be ignored via the @Ignore annotation in JUnit, for example.
     * Ignored tests are not considered the same as pending tests: a pending test is one that
     * has been specified, but the corresponding code is yet to be implemented, whereas an
     * ignored test can be a temporarily-deactivated test (during refactoring, for example).
     */
    IGNORED(2),

    /**
     * A pending test is one that has been specified but not yet implemented.
     * In a JUnit test case, you can use the (Thucydides) @Pending annotation to mark this.
     * A pending test case is a test case that has at least one pending test.
     */
    PENDING(2),

    /**
     * The test or test case ran as expected.
     */
    SUCCESS(1),
    
    /**
     * Test result not known yet.
     */
    UNDEFINED(0);

    private final int priority;

    TestResult(int priority) {
        this.priority = priority;
    }


    public boolean overrides(TestResult result) {
        return priority > result.priority;
    }
}
