package net.thucydides.core.model.stacktrace;

import net.serenitybdd.core.exceptions.SerenityWebDriverException;
import net.thucydides.core.webdriver.WebdriverAssertionError;

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
        if (!(thrownException instanceof WebdriverAssertionError) && ((thrownException instanceof SerenityWebDriverException) || (thrownException instanceof AssertionError))){
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
