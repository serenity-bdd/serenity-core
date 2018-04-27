package net.thucydides.core.steps;

import net.serenitybdd.core.exceptions.SerenityManagedException;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.failures.FailureAnalysis;
import net.thucydides.core.model.stacktrace.FailureCause;
import net.thucydides.core.model.stacktrace.RootCauseAnalyzer;

public class TestFailureCause {
    private final FailureCause rootCause;
    private final String testFailureClassname;
    private final String testFailureMessage;
    private final TestResult annotatedResult;
    private final transient Throwable originalCause;

    public TestFailureCause(Throwable originalCause, FailureCause rootCause, String testFailureClassname, String testFailureMessage, TestResult annotatedResult) {
        this.originalCause = originalCause;
        this.rootCause = rootCause;
        this.testFailureClassname = testFailureClassname;
        this.testFailureMessage = testFailureMessage;
        this.annotatedResult = annotatedResult;
    }

    public static TestFailureCause from(Throwable cause) {
        if (cause != null) {
            RootCauseAnalyzer rootCauseAnalyser = new RootCauseAnalyzer(SerenityManagedException.detachedCopyOf(cause));
            FailureCause rootCause = rootCauseAnalyser.getRootCause();
            String testFailureClassname = rootCauseAnalyser.getRootCause().getErrorType();
            String testFailureMessage = rootCauseAnalyser.getMessage();
            TestResult annotatedResult = new FailureAnalysis().resultFor(rootCause.exceptionClass());
            return new TestFailureCause(cause, rootCause, testFailureClassname, testFailureMessage, annotatedResult);
        } else {
            return new TestFailureCause(null, null, "", "", TestResult.UNDEFINED);
        }
    }

    public FailureCause getRootCause() {
        return rootCause;
    }

    public String getTestFailureClassname() {
        return testFailureClassname;
    }

    public String getTestFailureMessage() {
        return testFailureMessage;
    }

    public TestResult getAnnotatedResult() {
        return annotatedResult;
    }

    public boolean isDefined() {
        return (!testFailureClassname.isEmpty());
    }

    public Throwable getOriginalCause() { return originalCause; }
}
