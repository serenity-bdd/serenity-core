package net.thucydides.junit.runners;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;

public abstract class RunNotifierDecorator extends RunNotifier {
    
    protected abstract RunNotifier underlyingNotifier();

    @Override
    public void addListener(RunListener listener) {
        underlyingNotifier().addListener(listener);
    }

    @Override
    public void removeListener(RunListener listener) {
        underlyingNotifier().removeListener(listener);
    }

    @Override
    public void fireTestRunStarted(Description description) {
        underlyingNotifier().fireTestRunStarted(description);
    }

    @Override
    public void fireTestRunFinished(Result result) {
        underlyingNotifier().fireTestRunFinished(result);
    }

    @Override
    public void fireTestStarted(Description description) throws StoppedByUserException {
        underlyingNotifier().fireTestStarted(description);
    }

    @Override
    public void fireTestFailure(Failure failure) {
        underlyingNotifier().fireTestFailure(failure);
    }

    @Override
    public void fireTestAssumptionFailed(Failure failure) {
        underlyingNotifier().fireTestAssumptionFailed(failure);
    }

    @Override
    public void fireTestIgnored(Description description) {
        underlyingNotifier().fireTestIgnored(description);
    }

    @Override
    public void fireTestFinished(Description description) {
        underlyingNotifier().fireTestFinished(description);
    }

    @Override
    public void pleaseStop() {
        underlyingNotifier().pleaseStop();
    }

    @Override
    public void addFirstListener(RunListener listener) {
        underlyingNotifier().addFirstListener(listener);
    }
}
