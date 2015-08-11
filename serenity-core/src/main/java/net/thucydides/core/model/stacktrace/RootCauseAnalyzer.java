package net.thucydides.core.model.stacktrace;

import net.serenitybdd.core.exceptions.SerenityWebDriverException;

/**
 * Created by john on 3/07/2014.
 */
public class RootCauseAnalyzer {

    private final Throwable thrownException;

    public RootCauseAnalyzer(Throwable thrownException) {
        this.thrownException = thrownException;
    }

    public FailureCause getRootCause() {

        Throwable originalException = originalExceptionFrom(thrownException);
        StackTraceSanitizer stackTraceSanitizer = StackTraceSanitizer.forStackTrace(originalException.getStackTrace());
        return new FailureCause(originalException, stackTraceSanitizer.getSanitizedStackTrace());
    }

    private Throwable originalExceptionFrom(Throwable thrownException) {
        if (thrownException instanceof SerenityWebDriverException) {
            return thrownException;
        } else {
            return (thrownException.getCause() != null) ? thrownException.getCause() : thrownException;
        }
    }

    public String getClassname() {
        return getRootCause().getErrorType();
    }

    public String getMessage() {
        return getRootCause().getMessage();
    }
}
