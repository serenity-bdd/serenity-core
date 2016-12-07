package net.thucydides.core.model.stacktrace;

import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.exceptions.SerenityManagedException;
import net.thucydides.core.model.failures.FailureAnalysis;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.WebdriverAssertionError;

/**
 * Created by john on 3/07/2014.
 */
public class RootCauseAnalyzer {

    private final Throwable thrownException;
    private final FailureAnalysis failureAnalysis;
    private final EnvironmentVariables environmentVariables;

    public RootCauseAnalyzer(Throwable thrownException) {
        this.thrownException = thrownException;
        this.environmentVariables = ConfiguredEnvironment.getEnvironmentVariables();
        failureAnalysis = new FailureAnalysis(environmentVariables);
    }

    public FailureCause getRootCause() {

        Throwable originalException = originalExceptionFrom(thrownException);
        StackTraceSanitizer stackTraceSanitizer = StackTraceSanitizer.forStackTrace(originalException.getStackTrace());
        return new FailureCause(originalException, stackTraceSanitizer.getSanitizedStackTrace());
    }

    private Throwable originalExceptionFrom(Throwable thrownException) {

        if (!(thrownException instanceof WebdriverAssertionError) && ((thrownException instanceof SerenityManagedException) || (thrownException instanceof AssertionError))){
            return thrownException;
        }
        if (failureAnalysis.reportAsError(thrownException.getClass())) {
            return thrownException;
        }
        if (failureAnalysis.reportAsCompromised(thrownException.getClass())) {
            return thrownException;
        }
        if (failureAnalysis.reportAsFailure(thrownException.getClass())) {
            return thrownException;
        }
        if (failureAnalysis.reportAsPending(thrownException.getClass())) {
            return thrownException;
        }
        return (thrownException.getCause() != null) ? thrownException.getCause() : thrownException;
    }

    public String getClassname() {
        return getRootCause().getErrorType();
    }

    public String getMessage() {
        return (getRootCause().getMessage() != null) ?
                getRootCause().getMessage().replace("java.lang.AssertionError","") : getRootCause().getMessage();
    }
}
