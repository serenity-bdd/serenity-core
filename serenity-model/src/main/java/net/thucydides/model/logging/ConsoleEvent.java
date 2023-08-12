package net.thucydides.model.logging;

import net.thucydides.model.domain.TestResult;

public enum ConsoleEvent {
    TEST_STARTED("Test Started"),
    TEST_PASSED("Test Passed"),
    TEST_FAILED("Test Failed"),
    TEST_ERROR("Test Error"),
    TEST_COMPROMISED("Test Compromised"),
    TEST_ABORTED("Test Aborted"),
    TEST_PENDING("Test Pending"),
    TEST_SKIPPED("Test Skipped"),
    ;

    public static ConsoleEvent forTestResult(TestResult result) {
        switch (result) {
            case SUCCESS:
                return TEST_PASSED;
            case COMPROMISED:
                return TEST_COMPROMISED;
            case ERROR:
                return TEST_ERROR;
            case FAILURE:
                return TEST_FAILED;
            case PENDING:
                return TEST_PENDING;
            case ABORTED:
                return TEST_ABORTED;
            case IGNORED:
            default:
                return TEST_SKIPPED;
        }
    }

    private String title;

    ConsoleEvent(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
