package serenitymodel.net.thucydides.core.model.failures;

import serenitymodel.net.serenitybdd.core.exceptions.NoException;
import serenitymodel.net.thucydides.core.webdriver.WebdriverAssertionError;

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
