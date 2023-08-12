package net.thucydides.model.domain.stacktrace;

import com.google.common.base.Splitter;
import net.serenitybdd.model.exceptions.CausesCompromisedTestFailure;
import net.serenitybdd.model.exceptions.SerenityManagedException;
import net.serenitybdd.model.exceptions.TestCompromisedException;
import net.serenitybdd.model.exceptions.UnrecognisedException;
import net.thucydides.model.domain.TestFailureException;
import net.thucydides.model.util.NameConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class FailureCause {

    public static final String ERROR_MESSAGE_LABEL_1 = "{'errorMessage':";
    public static final String ERROR_MESSAGE_LABEL_2 = "{\"errorMessage\":";

    private final static List<String> COLLAPSE_NEW_LINE_HINTS = Arrays.asList(
            "AssertionError",
            "Expected",
            "Expecting",
            "ComparisonFailure");

    private String errorType;
    private String message;
    private StackTraceElement[] stackTrace;
    private FailureCause rootCause;

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
        this.rootCause = rootCauseOf(cause);
    }

    private FailureCause rootCauseOf(Throwable cause) {
        if (cause.getCause() == null || cause.getCause() == cause) {
            return null;
        }

        StackTraceSanitizer stackTraceSanitizer = StackTraceSanitizer.forStackTrace(cause.getCause().getStackTrace());
        return new FailureCause(cause.getCause(), stackTraceSanitizer.getSanitizedStackTrace());
    }

    public FailureCause(Throwable cause, StackTraceElement[] stackTrace) {
        this(cause, exceptionClassName(cause), cause.getMessage(), stackTrace);
    }

    public FailureCause(Throwable cause, String exceptionClassName, String message, StackTraceElement[] stackTrace) {
        this.errorType = exceptionClassName;
        this.message = message;
        this.stackTrace = stackTrace;
        this.originalCause = cause;
        this.rootCause = rootCauseOf(cause);
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

    public Optional<FailureCause> getRootCause() {
        return Optional.ofNullable(rootCause);

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
            return Optional.ofNullable(exception);
        } catch (Exception e) {
            Throwable exception = new UnrecognisedException();
            exception.setStackTrace(this.getStackTrace());
            return Optional.ofNullable(exception);
        }

    }

    private <T extends Throwable> T buildThrowable(String testFailureMessage, Class failureClass) throws Exception {

        if (stringConstructorFor(failureClass).isPresent()) {
            return (T) stringConstructorFor(failureClass).get().newInstance(testFailureMessage);
        } else if (stringThrowableConstructorFor(failureClass).isPresent()) {
            try {
                return (T) stringThrowableConstructorFor(failureClass).get().newInstance(testFailureMessage, null);
            } catch (Throwable requiresNonNullExceptionCause) {
                return (T) stringThrowableConstructorFor(failureClass).get().newInstance(testFailureMessage, reconstructedExceptionFor(failureClass));
            }
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
            return Optional.ofNullable(failureClass.getConstructor());
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    private Optional<Constructor> stringConstructorFor(Class failureClass) throws NoSuchMethodException {
        try {
            return Optional.ofNullable(failureClass.getConstructor(String.class));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    private Optional<Constructor> stringThrowableConstructorFor(Class failureClass) {
        return Arrays.stream(failureClass.getDeclaredConstructors()).filter(
                constructor -> constructor.getParameterTypes().length == 2
                        && String.class.isAssignableFrom(constructor.getParameterTypes()[0])
                        && Throwable.class.isAssignableFrom(constructor.getParameterTypes()[1])
        ).findFirst();
    }

    private Object reconstructedExceptionFor(Class failureClass) {
        try {
            Optional<Constructor> constructor = stringThrowableConstructorFor(failureClass);
            if (constructor.isPresent()) {
                try {
                    Object dummyException = constructor.get().getParameterTypes()[1].getDeclaredConstructor().newInstance();
                    if (dummyException instanceof Throwable) {
                        ((Throwable)dummyException).setStackTrace(new StackTraceElement[]{});
                    }
                    return dummyException;
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    return null;
                }
            }
        } catch(Throwable couldNotCreateADummyException) {
        }
        return null;
    }


    private Optional<Constructor> throwableConstructorFor(Class failureClass) throws NoSuchMethodException {
        try {
            return Optional.ofNullable(failureClass.getConstructor(Throwable.class));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
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

    public boolean isAnError() {
        return (getOriginalCause() instanceof Error) && (!(getOriginalCause() instanceof AssertionError));
    }

    public boolean isCompromised() {
        return (getOriginalCause() instanceof CausesCompromisedTestFailure);
    }

    public boolean isAnAssertionError() {
        return (getOriginalCause() instanceof AssertionError);
    }

    public Throwable asException() {
        return getOriginalCause();
    }

    public RuntimeException asRuntimeException() {
        Throwable cause = getOriginalCause();
        if (isCompromised()) {
            throw asCompromisedException();
        } else if (isAnError()) {
            throw asError();
        } else if (isAnAssertionError()) {
            throw asAssertionError();
        } else if (getOriginalCause() instanceof RuntimeException) {
            throw (RuntimeException) getOriginalCause();
        } else {
            throw asFailure();
        }

    }

    public SerenityManagedException asFailure() {
        return new SerenityManagedException(getOriginalCause());
    }

    public Error asAssertionError() {
        if ((getOriginalCause() instanceof AssertionError)) {
            return (AssertionError) getOriginalCause();
        } else {
            return new AssertionError(getOriginalCause());
        }
    }

    public Error asError() {
        if (isAnError()) {
            return (Error) getOriginalCause();
        } else {
            return new Error(getOriginalCause());
        }
    }

    public RuntimeException asCompromisedException() {
        if (originalCause instanceof RuntimeException) {
            throw (RuntimeException) originalCause;
        } else {
            throw new TestCompromisedException(originalCause);
        }
    }
}
