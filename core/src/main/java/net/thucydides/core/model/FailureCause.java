package net.thucydides.core.model;

import com.google.common.base.Optional;

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
        this.errorType = cause.getClass().getName();
        this.message =  cause.getMessage();
        this.stackTrace = cause.getStackTrace();
    }

    public FailureCause(Throwable cause, StackTraceElement[] stackTrace) {
        this(cause.getClass().getName(), cause.getMessage(), stackTrace);
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
        Optional<Throwable> exception = restoreExceptionFrom(errorType, message);
        if (exception.isPresent()) {
            return exception.get();
        } else {
            return new TestFailureException(errorType + ":" + message);
        }
    }

    private Optional<Throwable> restoreExceptionFrom(String testFailureClassname, String testFailureMessage) {
        try {
            Class failureClass = Class.forName(testFailureClassname);
            Constructor constructorWithMessage = getExceptionConstructor(failureClass);
            Throwable exception = (Throwable) constructorWithMessage.newInstance(testFailureMessage);
            exception.setStackTrace(this.getStackTrace());
            return Optional.of(exception);
        } catch (Exception e) {
            Throwable exception = new RuntimeException(testFailureClassname + ": " + testFailureMessage);
            exception.setStackTrace(this.getStackTrace());
            return Optional.of(exception);
//            return Optional.absent();
        }

    }

    private Constructor getExceptionConstructor(Class failureClass) throws NoSuchMethodException {
        try {
            return failureClass.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
            return failureClass.getConstructor(Object.class);
        }
    }

}
