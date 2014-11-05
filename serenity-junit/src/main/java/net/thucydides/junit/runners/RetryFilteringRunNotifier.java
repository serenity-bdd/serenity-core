package net.thucydides.junit.runners;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        log.debug("Test failed: " + failure);
        testStartAlreadyFired = false;
        testFailed = true;
        lastFailure = failure;

        retryAwareRunNotifier.fireTestFailure(failure);
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
        super.fireTestRunStarted(description);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void fireTestRunFinished(Result result) {
        super.fireTestRunFinished(result);    //To change body of overridden methods use File | Settings | File Templates.

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
