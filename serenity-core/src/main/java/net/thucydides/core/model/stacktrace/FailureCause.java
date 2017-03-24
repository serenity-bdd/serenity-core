package net.thucydides.core.model.stacktrace;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import net.serenitybdd.core.exceptions.SerenityManagedException;
import net.serenitybdd.core.exceptions.UnrecognisedException;
import net.thucydides.core.model.TestFailureException;
import net.thucydides.core.util.NameConverter;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class FailureCause {

    public static final String ERROR_MESSAGE_LABEL_1 = "{'errorMessage':";
    public static final String ERROR_MESSAGE_LABEL_2 = "{\"errorMessage\":";

    private final static List<String> COLLAPSE_NEW_LINE_HINTS = ImmutableList.of(
            "AssertionError",
            "Expected",
            "Expecting",
            "ComparisonFailure");

    private String errorType;
    private String message;
    private StackTraceElement[] stackTrace;
    /**
     * Used within a test run but not stored for reporting
     */
    private transient Throwable originalCause;

    public FailureCause() {
    }

    public FailureCause(Throwable cause) {
        this.errorType = exceptionClassName(cause);
        this.message = cause.getMessage();
        this.stackTrace = cause.getStackTrace();
        this.originalCause = cause;
    }

    public FailureCause(Throwable cause, StackTraceElement[] stackTrace) {
        this(cause, exceptionClassName(cause), cause.getMessage(), stackTrace);
    }

    public FailureCause(Throwable cause, String exceptionClassName, String message, StackTraceElement[] stackTrace) {
        this.errorType = exceptionClassName;
        this.message = message;
        this.stackTrace = stackTrace;
        this.originalCause = cause;

    }

    private static String exceptionClassName(Throwable cause) {
        if (cause instanceof SerenityManagedException) {
            return ((SerenityManagedException) cause).getExceptionClass().getName();
        } else {
            return cause.getClass().getName();
        }
    }

    public FailureCause(String errorType, String message, StackTraceElement[] stackTrace) {
        this.errorType = errorType;
        this.message = parseErrorMessage(message);
        this.stackTrace = (stackTrace == null) ? null : Arrays.copyOf(stackTrace,stackTrace.length);
    }

    private String parseErrorMessage(String message) {
        if ((message != null) && (message.startsWith(ERROR_MESSAGE_LABEL_1) || message.startsWith(ERROR_MESSAGE_LABEL_2))) {
            return extractErrorMessageTextFrom(message);
        } else {
            return message;
        }
    }

    private String extractErrorMessageTextFrom(String message) {
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

    public Throwable getOriginalCause() { return originalCause; }

    public String getSimpleErrorType() {
        return NameConverter.humanize(lastElementOf(Splitter.on(".").splitToList(errorType)));
    }

    private String lastElementOf(List<String> elements) {
        return elements.get(elements.size() - 1);
    }

    public String getMessage() {
        return message;
    }

    public String asString() {
        return isEmpty(message) ? errorType : message;
    }

    public StackTraceElement[] getStackTrace() {
        return Arrays.copyOf(stackTrace, stackTrace.length);
    }

    public void setStackTrace(StackTraceElement[] stackTrace) {
        this.stackTrace = Arrays.copyOf(stackTrace,stackTrace.length);
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
                exception = new UnrecognisedException();
            }
            exception.setStackTrace(this.getStackTrace());
            return Optional.fromNullable(exception);
        } catch (Exception e) {
            Throwable exception = new UnrecognisedException();
            exception.setStackTrace(this.getStackTrace());
            return Optional.fromNullable(exception);
        }

    }

    private <T extends Throwable> T buildThrowable(String testFailureMessage, Class failureClass) throws Exception {

        if (stringConstructorFor(failureClass).isPresent()) {
            return (T) stringConstructorFor(failureClass).get().newInstance(testFailureMessage);
        } else if (stringThrowableConstructorFor(failureClass).isPresent()) {
            return (T) stringThrowableConstructorFor(failureClass).get().newInstance(testFailureMessage, null);
        } else if (throwableConstructorFor(failureClass).isPresent()) {
            return (T) throwableConstructorFor(failureClass).get().newInstance(new AssertionError(testFailureMessage));
        } else if (AssertionError.class.isAssignableFrom(failureClass)) {
            return (T) new AssertionError(testFailureMessage);
        } else if (defaultConstructorFor(failureClass).isPresent()) {
            return (T) defaultConstructorFor(failureClass).get().newInstance();
        }
        return null;
    }

    private Optional<Constructor> defaultConstructorFor(Class failureClass) throws NoSuchMethodException {
        try {
            return Optional.fromNullable(failureClass.getConstructor());
        } catch (NoSuchMethodException e) {
            return Optional.absent();
        }
    }

    private Optional<Constructor> stringConstructorFor(Class failureClass) throws NoSuchMethodException {
        try {
            return Optional.fromNullable(failureClass.getConstructor(String.class));
        } catch (NoSuchMethodException e) {
            return Optional.absent();
        }
    }

    private Optional<Constructor> stringThrowableConstructorFor(Class failureClass) throws NoSuchMethodException {
        try {
            return Optional.fromNullable(failureClass.getConstructor(String.class, Throwable.class));
        } catch (NoSuchMethodException e) {
            return Optional.absent();
        }
    }

    private Optional<Constructor> throwableConstructorFor(Class failureClass) throws NoSuchMethodException {
        try {
            return Optional.fromNullable(failureClass.getConstructor(Throwable.class));
        } catch (NoSuchMethodException e) {
            return Optional.absent();
        }
    }

    public String getShortenedMessage() {

        if (message == null) { return ""; }
        if (isEmpty(message.trim())) { return message; }

        String[] messageLines = withCollapedAssertionError(message).trim().split("\n|\n\r|\r");
        return messageLines[0];
    }

    private String withCollapedAssertionError(String message) {
        if (shouldCollapseAssertionsFor(message)) {
            return message.replace("\r\n", "")
                            .replace("\\r\\n", "")
                            .replace("/r/n", "")
                            .replace("\n", "")
                            .replace("\\n", "")
                            .replace("/n", "")
                            .replace("     ", " ")
                            .replace("java.lang.AssertionError", "");
        }
        return message;
    }

    private boolean shouldCollapseAssertionsFor(String message) {
        for(String collapseHint : COLLAPSE_NEW_LINE_HINTS) {
            if (message.toLowerCase().contains(collapseHint.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
