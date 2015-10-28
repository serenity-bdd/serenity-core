package net.thucydides.core.model;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import cucumber.api.PendingException;
import net.serenitybdd.core.PendingStepException;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.StepFailureException;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.WebdriverAssertionError;
import net.thucydides.core.webdriver.exceptions.CausesAssertionFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.thucydides.core.model.TestResult.*;

/**
 * Determine whether a given type of exception should result in a failure or an error.
 * By default, any exception  that extends AssertionError is a FAILURE.
 * Any exception  that extends WebdriverAssertionError and has a cause that is an AssertionError is also a FAILURE.
 * All other exceptions are an ERROR (except for StepFailureException as described below)
 *
 * Any exception that extends StepFailureException and has a cause that meets the above criteria is classed as above.
 * All other exceptions are an ERROR
 *
 * You can specify your own exceptions that will cause a failure by using the serenity.fail.on property.
 * You can also specify those that will cause an error using serenity.error.on.
 */
public class FailureAnalysis {

    private final EnvironmentVariables environmentVariables;

    private static final Logger LOGGER = LoggerFactory.getLogger(FailureAnalysis.class);

    public FailureAnalysis() {
        this(Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    public FailureAnalysis(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public TestResult resultFor(Class testFailureCause) {
        if (isA(PendingStepException.class, testFailureCause)) {
            return PENDING;
        }
        if (reportAsFailure(testFailureCause)) {
            return FAILURE;
        }
        return ERROR;
    }

    private final List<Class<?>> DEFAULT_FAILURE_TYPES = ImmutableList.of(AssertionError.class, CausesAssertionFailure.class);
    private final List<Class<? extends RuntimeException>> DEFAULT_PENDING_TYPES
            = ImmutableList.of(PendingStepException.class, PendingException.class);

    public boolean reportAsFailure(Class<?> testFailureCause) {
        if (testFailureCause == null) {
            return false;
        }
        for(Class<?> validFailureType: getConfiguredFailureTypes()) {
            if (isA(validFailureType,testFailureCause)) {
                return true;
            }
        }
        return false;
    }

    public boolean reportAsPending(Class<?> testFailureCause) {
        if (testFailureCause == null) {
            return false;
        }
        for(Class<?> validPendingType: getConfiguredPendingTypes()) {
            if (isA(validPendingType,testFailureCause)) {
                return true;
            }
        }
        return false;
    }

    private List<Class<?>> getConfiguredFailureTypes() {
        List<Class<?>> failureTypes = Lists.newArrayList(DEFAULT_FAILURE_TYPES);

        failureTypes.addAll(failureTypesDefinedIn(environmentVariables));
        failureTypes.removeAll(errorTypesDefinedIn(environmentVariables));

        return failureTypes;
    }

    private List<Class<? extends RuntimeException>> getConfiguredPendingTypes() {
        List<Class<? extends RuntimeException>> pendingTypes = Lists.newArrayList(DEFAULT_PENDING_TYPES);
        pendingTypes.addAll(pendingTypesDefinedIn(environmentVariables));
        return pendingTypes;
    }

    private List<Class<? extends RuntimeException>> errorTypesDefinedIn(EnvironmentVariables environmentVariables) {
        return typesDefinedIn(ThucydidesSystemProperty.SERENITY_ERROR_ON, environmentVariables);
    }


    private List<Class<? extends RuntimeException>> failureTypesDefinedIn(EnvironmentVariables environmentVariables) {
        return typesDefinedIn(ThucydidesSystemProperty.SERENITY_FAIL_ON, environmentVariables);
    }

    private List<Class<? extends RuntimeException>> pendingTypesDefinedIn(EnvironmentVariables environmentVariables) {
        return typesDefinedIn(ThucydidesSystemProperty.SERENITY_PENDING_ON, environmentVariables);
    }

    private List<Class<? extends RuntimeException>> typesDefinedIn(ThucydidesSystemProperty typeListProperty, EnvironmentVariables environmentVariables) {

        List<Class<? extends RuntimeException>> errorTypes  = Lists.newArrayList();
        List<String> errorClassNames = Splitter.on(",")
                .trimResults()
                .splitToList(typeListProperty.from(environmentVariables,""));

        for(String errorClassName : errorClassNames) {
            try {
                errorTypes.add((Class<? extends RuntimeException>) Class.forName(errorClassName));
            } catch (ClassNotFoundException e) {
                LOGGER.warn("Could not find error class: " + errorClassName);
            }
        }
        return errorTypes;
    }

    private boolean isA(Class<?> expectedClass, Class testFailureCause) {
        return expectedClass.isAssignableFrom(testFailureCause);
    }

    public TestResult resultFor(Throwable testFailureCause) {
        if (isPendingException(testFailureCause)) {
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
        if (stepFailure.getExceptionClass() == null) {
            return FAILURE;
        } else {
            return resultFor(stepFailure.getExceptionClass());
        }
    }

    private boolean failingStepException(Throwable testFailureCause) {
        return ((StepFailureException.class.isAssignableFrom(testFailureCause.getClass()))
                && (testFailureCause.getCause() != null)
                && (isFailureError(testFailureCause.getCause())));
    }

    private boolean isFailureError(Throwable testFailureCause) {
        return reportAsFailure(getRootCauseOf(testFailureCause));
    }

    private boolean isPendingException(Throwable testFailureCause) {
        return reportAsPending(getRootCauseOf(testFailureCause));
    }

    private Class<? extends Throwable> getRootCauseOf(Throwable testFailureCause) {
        Class<? extends Throwable> failureCauseClass = testFailureCause.getClass();

        if(WebdriverAssertionError.class.isAssignableFrom(failureCauseClass) && (testFailureCause.getCause() != null)) {
            failureCauseClass = testFailureCause.getCause().getClass();
        }
        return failureCauseClass;
    }

}
