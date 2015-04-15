package net.thucydides.core.model.stacktrace;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.exceptions.SerenityWebDriverException;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.List;

import static net.thucydides.core.ThucydidesSystemProperty.SIMPLIFIED_STACK_TRACES;

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
        // (thrownException.getCause() != null) ? thrownException.getCause() : thrownException;

        StackTraceSanitizer stackTraceSanitizer = StackTraceSanitizer.forStackTrace(thrownException.getStackTrace());
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
