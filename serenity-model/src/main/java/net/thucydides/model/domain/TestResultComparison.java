package net.thucydides.model.domain;

import static net.thucydides.model.domain.TestResult.*;

/**
 * A list of test results, used to determine the overall test result.
 */
public class TestResultComparison {

    private final TestResult testResultA;
    private final TestResult testResultB;

    public TestResultComparison(TestResult testResultA, TestResult testResultB) {

        this.testResultA = testResultA;
        this.testResultB = testResultB;
    }

    public static TestResult overallResultFor(TestResult testResultA, TestResult testResultB) {
        if (testResultA == COMPROMISED || testResultB == COMPROMISED) {
            return COMPROMISED;
        }

        if (testResultA == ERROR || testResultB == ERROR) {
            return ERROR;
        }

        if (testResultA == FAILURE || testResultB == FAILURE) {
            return FAILURE;
        }

        if (testResultA == PENDING || testResultB == PENDING) {
            return PENDING;
        }

        if (containsOnly(testResultA, testResultB, IGNORED)) {
            return IGNORED;
        }

        if (containsOnly(testResultA, testResultB, SKIPPED)) {
            return SKIPPED;
        }

        if (containsOnly(SUCCESS, IGNORED, SKIPPED)) {
            return SUCCESS;
        }
        return SUCCESS;
    }

    private static boolean containsOnly(TestResult testResultA, TestResult testResultB, final TestResult... expectedValues) {
        for (TestResult expectedValue : expectedValues) {
            if ((testResultA != expectedValue) || (testResultB != expectedValue)) {
                return false;
            }
        }
        return true;
    }

}
