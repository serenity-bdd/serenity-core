package net.thucydides.core.model;

import net.thucydides.core.PendingStepException;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.StepFailureException;
import net.thucydides.core.webdriver.WebdriverAssertionError;

import static net.thucydides.core.model.TestResult.*;

/**
 * Determine whether a given type of exception should result in a failure or an error.
 * Any exception  that extends AssertionError is a FAILURE.
 * Any exception  that extends WebdriverAssertionError and has a cause that is an AssertionError is also a FAILURE.
 * All other exceptions are an ERROR (except for StepFailureException as described below)
 *
 * Any exception that extends StepFailureException and has a cause that meets the above criteria is classed as above.
 * All other exceptions are an ERROR
 */
public class FailureAnalysis {
    // TODO: Finish (or not)
    public TestResult resultFor(Class testFailureCause) {
        if (isA(PendingStepException.class, testFailureCause)) {
            return PENDING;
        }
        if (isFailure(testFailureCause.getName())) {
            return FAILURE;
        }
        return ERROR;
    }

    public boolean isFailure(String testFailureCause) {
        if (testFailureCause != null) {
            try {
                if (isA(AssertionError.class, Class.forName(testFailureCause))) {
                    return true;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean isA(Class<?> expectedClass, Class testFailureCause) {
        while(testFailureCause != null) {
            if (testFailureCause.equals(expectedClass)) {
                return true;
            }
            testFailureCause = testFailureCause.getSuperclass();
        }
        return false;
    }

    public TestResult resultFor(Throwable testFailureCause) {
        if (PendingStepException.class.isAssignableFrom(testFailureCause.getClass())) {
            return PENDING;
        } else if (isFailureError(testFailureCause)) {
            return FAILURE;
        } else if (failingStepException(testFailureCause)) {
            return FAILURE;
        } else {
            return ERROR;
        }
    }

    public TestResult resultFor(StepFailure stepFailure) {
        if (stepFailure.getException() == null) {
            return FAILURE;
        } else {
            return resultFor(stepFailure.getException());
        }
    }

    private boolean failingStepException(Throwable testFailureCause) {
        return ((StepFailureException.class.isAssignableFrom(testFailureCause.getClass()))
                && (testFailureCause.getCause() != null)
                && (isFailureError(testFailureCause.getCause())));
    }

    private boolean isFailureError(Throwable testFailureCause) {
        Class<? extends Throwable> failureCauseClass = testFailureCause.getClass();

        if(WebdriverAssertionError.class.isAssignableFrom(failureCauseClass)) {
            return testFailureCause.getCause() == null || AssertionError.class.isAssignableFrom(testFailureCause.getCause().getClass());
        }
        return AssertionError.class.isAssignableFrom(failureCauseClass);
    }
}