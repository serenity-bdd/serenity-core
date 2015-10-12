package net.serenitybdd.junit.runners;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class RetryFilteringRunNotifier extends RunNotifierDecorator {

    private static final Logger log = LoggerFactory.getLogger(RetryFilteringRunNotifier.class);

    private RunNotifier target;
    private RunNotifier retryAwareRunNotifier;

    private boolean testFailed = false;
    private Failure lastFailure;

    private boolean testAssumptionFailed = false;
    private Failure lastAssumptionFailure;

    private boolean testStartAlreadyFired = false;
    private Description lastIgnored;
    private Description lastDescription;

    public RetryFilteringRunNotifier(RunNotifier target, RunNotifier retryAwareRunNotifier) {
        this.target = target;
        this.retryAwareRunNotifier = retryAwareRunNotifier;
    }

    @Override
    public void fireTestStarted(Description description) throws StoppedByUserException {
        log.debug("Test started: " + description);
        if (!testStartAlreadyFired) {
            super.fireTestStarted(description);
        }

        testStartAlreadyFired = true;

        retryAwareRunNotifier.fireTestStarted(description);
    }

    @Override
    public void fireTestFailure(Failure failure) {
        if (isExpected(failure)) {
            fireTestFinished(failure.getDescription());
        } else {
            log.debug("Test failed: " + failure, failure.getException());
            testStartAlreadyFired = false;
            testFailed = true;
            lastFailure = failure;
            retryAwareRunNotifier.fireTestFailure(failure);
        }
    }

    private boolean isExpected(Failure failure) {
        if ((failure.getDescription().getTestClass() == null) || (failure.getDescription().getMethodName() == null)) {
            return false;
        }
        try {
            Method testMethod =  failure.getDescription().getTestClass().getMethod(failure.getDescription().getMethodName());
            Test testAnnotation = testMethod.getAnnotation(Test.class);
            if (testAnnotation.expected() != null) {
                return (failure.getException().getClass().isAssignableFrom(testAnnotation.expected()));
            }
        } catch (NoSuchMethodException e) {
            return false;
        }
        return false;
    }

    public void flush() {
        log.debug("Flushing notifications");

        if (testFailed) {
            super.fireTestFailure(lastFailure);
        }

        if (testAssumptionFailed) {
            super.fireTestAssumptionFailed(lastAssumptionFailure);
        }

        if (lastIgnored != null) {
            super.fireTestIgnored(lastIgnored);
        }

        if (lastDescription != null) {
            super.fireTestFinished(lastDescription);
        }
    }

    public void reset() {
        testFailed = false;
        testAssumptionFailed = false;
        lastIgnored = null;
        lastDescription = null;
    }

    @Override
    public void fireTestFinished(Description description) {
        testStartAlreadyFired = false;
        log.debug("Test finished: " + description);
        lastDescription = description;

        retryAwareRunNotifier.fireTestFinished(description);
    }

    @Override
    public void fireTestIgnored(Description description) {
        log.debug("Test ignored: " + description);
        lastIgnored = description;
        if (!testStartAlreadyFired) {
            super.fireTestStarted(description);
        }
        testStartAlreadyFired = false;
        retryAwareRunNotifier.fireTestIgnored(description);
    }

    @Override
    public void fireTestAssumptionFailed(Failure failure) {
        log.debug("Test assumption failed: " + failure);
        lastAssumptionFailure = failure;
        testAssumptionFailed = true;

        retryAwareRunNotifier.fireTestAssumptionFailed(failure);
    }

    @Override
    public void fireTestRunStarted(Description description) {
        super.fireTestRunStarted(description);
    }

    @Override
    public void fireTestRunFinished(Result result) {
        super.fireTestRunFinished(result);

        retryAwareRunNotifier.fireTestRunFinished(result);
    }

    @Override
    protected RunNotifier underlyingNotifier() {
        return target;
    }

    public boolean lastTestFailed() {
        return lastFailure != null;
    }

    public void clearLastFailure() {
        testFailed = false;
        lastFailure = null;

        testAssumptionFailed = false;
        lastAssumptionFailure = null;
    }

}
