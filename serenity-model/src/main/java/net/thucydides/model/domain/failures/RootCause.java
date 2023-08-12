package net.thucydides.model.domain.failures;

import net.serenitybdd.model.exceptions.NoException;
import net.thucydides.model.webdriver.WebdriverAssertionError;

public class RootCause {

    public static Class<? extends Throwable> ofException(Throwable testFailureCause) {
        if (testFailureCause == null) { return NoException.class; }

        Class<? extends Throwable> failureCauseClass = testFailureCause.getClass();

        if(WebdriverAssertionError.class.isAssignableFrom(failureCauseClass) && (testFailureCause.getCause() != null)) {
            failureCauseClass = testFailureCause.getCause().getClass();
        }
        return failureCauseClass;
    }

}
