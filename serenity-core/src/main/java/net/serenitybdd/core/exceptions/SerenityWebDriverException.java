package net.serenitybdd.core.exceptions;


import org.openqa.selenium.WebDriverException;

public class SerenityWebDriverException extends WebDriverException {

    private final String detailedMessage;
    private final StackTraceElement[] stackTrace;
    private final Throwable cause;
    private final Class exceptionClass;


    public static Throwable detachedCopyOf(Throwable testErrorException) {
        if (!(testErrorException instanceof WebDriverException)) {
            return testErrorException;
        } else if (testErrorException instanceof SerenityWebDriverException) {
            return testErrorException;
        } else {
            return new SerenityWebDriverException((WebDriverException) testErrorException);
        }
    }


    public SerenityWebDriverException(WebDriverException testErrorException) {
        super(testErrorException.getCause());
        this.detailedMessage = testErrorException.getMessage();
        this.stackTrace = testErrorException.getStackTrace();
        this.cause = testErrorException.getCause();
        this.exceptionClass = testErrorException.getClass();
    }

    @Override
    public String getMessage() {
        return detailedMessage;
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return stackTrace.clone();
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    public Class getExceptionClass() {
        return exceptionClass;
    }
}