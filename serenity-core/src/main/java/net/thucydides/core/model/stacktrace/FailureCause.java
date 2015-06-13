package net.thucydides.core.model.stacktrace;

import com.google.common.base.Optional;
import net.serenitybdd.core.exceptions.SerenityWebDriverException;
import net.serenitybdd.core.exceptions.UnrecognisedException;
import net.thucydides.core.model.TestFailureException;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class FailureCause {

    public static final String ERROR_MESSAGE_LABEL_1 = "{'errorMessage':";
    public static final String ERROR_MESSAGE_LABEL_2 = "{\"errorMessage\":";

    private String errorType;
    private String message;
    private StackTraceElement[] stackTrace;

    public FailureCause() {}

    public FailureCause(Throwable cause) {
        this.errorType = exceptionClassName(cause);
        this.message =  cause.getMessage();
        this.stackTrace = cause.getStackTrace();
    }

    public FailureCause(Throwable cause, StackTraceElement[] stackTrace) {
        this(exceptionClassName(cause), cause.getMessage(), stackTrace);
    }

    private static String exceptionClassName(Throwable cause) {
        if (cause instanceof SerenityWebDriverException) {
            return ((SerenityWebDriverException) cause).getExceptionClass().getName();
        } else {
            return cause.getClass().getName();
        }
    }

    public FailureCause(String errorType, String message, StackTraceElement[] stackTrace) {
        this.errorType = errorType;
        this.message = parseErrorMessage(message);
        this.stackTrace = stackTrace;
    }

    private String parseErrorMessage(String message) {
        if ((message != null) && (message.startsWith(ERROR_MESSAGE_LABEL_1) || message.startsWith(ERROR_MESSAGE_LABEL_2))) {
                return extractErrorMessageTextFrom(message);
        } else {
            return message;
        }
    }

    private String extractErrorMessageTextFrom(String message)  {
        message = message.substring(ERROR_MESSAGE_LABEL_1.length() + 1);
        int endOfMessage = message.indexOf("','");
        if (endOfMessage == -1) {
            endOfMessage = message.indexOf("\",\"");
        }
        if (endOfMessage > 0) {
            return message.substring(0, endOfMessage);
        }
        return message;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getMessage() {
        return message;
    }

    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(StackTraceElement[] stackTrace) {
        this.stackTrace = stackTrace;
    }

    public Class<? extends Throwable> exceptionClass() {
        try {
            return (Class<? extends Throwable>) Class.forName(errorType);
        } catch (ClassNotFoundException e) {
            return Throwable.class;
        }
    }

    @Override
    public String toString() {
        return "FailureCause{" +
                "errorType='" + errorType + '\'' +
                ", message='" + message + '\'' +
                ", stackTrace=" + Arrays.toString(stackTrace) +
                '}';
    }

    public Throwable toException() {
        Optional<? extends Throwable> exception = restoreExceptionFrom(errorType, message);
        if (exception.isPresent()) {
            return exception.get();
        } else {
            return new TestFailureException(errorType + ":" + message);
        }
    }

    private Optional<? extends Throwable> restoreExceptionFrom(String testFailureClassname, String testFailureMessage) {
        try {
            Class failureClass = Class.forName(testFailureClassname);
            Throwable exception = buildThrowable(testFailureMessage, failureClass);
            if (exception == null) {
                exception =  new UnrecognisedException(failureClass.getName() + ": " + testFailureMessage);
            }
            exception.setStackTrace(this.getStackTrace());
            return Optional.fromNullable(exception);
        } catch (Exception e) {
            Throwable exception = new UnrecognisedException(testFailureClassname + ": " + testFailureMessage);
            exception.setStackTrace(this.getStackTrace());
            return Optional.fromNullable(exception);
        }

    }

    private <T extends Throwable> T buildThrowable(String testFailureMessage, Class failureClass) throws Exception {

        if (defaultConstructorFor(failureClass).isPresent()) {
            return (T) defaultConstructorFor(failureClass).get().newInstance();
        } else if (stringConstructorFor(failureClass).isPresent()) {
            return (T) stringConstructorFor(failureClass).get().newInstance(testFailureMessage);
        } else if (stringThrowableConstructorFor(failureClass).isPresent()) {
            return (T) stringThrowableConstructorFor(failureClass).get().newInstance(testFailureMessage, null);
        }else if (throwableConstructorFor(failureClass).isPresent()) {
            return (T) throwableConstructorFor(failureClass).get().newInstance(new AssertionError(testFailureMessage));
        }
        return null;
    }

    private Optional<Constructor> defaultConstructorFor(Class failureClass) throws NoSuchMethodException {
        try {
            return Optional.fromNullable(failureClass.getConstructor());
        } catch(NoSuchMethodException e) {
            return Optional.absent();
        }
    }

    private Optional<Constructor> stringConstructorFor(Class failureClass) throws NoSuchMethodException {
        try {
            return Optional.fromNullable(failureClass.getConstructor(String.class));
        } catch(NoSuchMethodException e) {
            return Optional.absent();
        }
    }

    private Optional<Constructor> stringThrowableConstructorFor(Class failureClass) throws NoSuchMethodException {
        try {
            return Optional.fromNullable(failureClass.getConstructor(String.class, Throwable.class));
        } catch(NoSuchMethodException e) {
            return Optional.absent();
        }
    }

    private Optional<Constructor> throwableConstructorFor(Class failureClass) throws NoSuchMethodException {
        try {
            return Optional.fromNullable(failureClass.getConstructor(Throwable.class));
        } catch(NoSuchMethodException e) {
            return Optional.absent();
        }
    }

    private Optional<Constructor> objectThrowableConstructorFor(Class failureClass) throws NoSuchMethodException {
        try {
            return Optional.fromNullable(failureClass.getConstructor(String.class, Throwable.class));
        } catch(NoSuchMethodException e) {
            return Optional.absent();
        }
    }

}
