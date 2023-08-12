package net.thucydides.model.steps;


import net.serenitybdd.model.exceptions.SerenityManagedException;

import java.util.Arrays;

/**
 * Description and underlying cause behind a step failure.
 * A <code>StepFailure</code> holds a description of the failed test step and the
 * exception that was thrown while running it. In most cases the Description
 * will be of a single test step.
 */
public class StepFailure {

    private final ExecutedStepDescription description;

    private final Throwable cause;
    private final Class<? extends Throwable> exceptionClass;
    private final String message;
    private final StackTraceElement[] stackTraceElements;


    public StepFailure(final ExecutedStepDescription description, final Throwable cause) {
        this.description = description;
        this.cause = cause;
        if (cause != null) {
            if (cause instanceof SerenityManagedException) {
                this.exceptionClass = ((SerenityManagedException)cause).getExceptionClass();
                this.message = cause.getMessage();
                this.stackTraceElements = cause.getStackTrace();
            } else {
                this.exceptionClass = cause.getClass();
                this.message = cause.getMessage();
                this.stackTraceElements = cause.getStackTrace();
            }
        } else {
            this.exceptionClass = null;
            this.message = null;
            this.stackTraceElements = null;
        }
    }

    /**
     * @return the raw description of the context of the failure.
     */
    public ExecutedStepDescription getDescription() {
        return description;
    }

    /**
     * @return the exception thrown
     */

    public Throwable getException() {
        return cause;
    }

    public String getMessage() {
        return message;
    }

    public Class<? extends Throwable> getExceptionClass() {
        return exceptionClass;
    }

    public StackTraceElement[] getStackTraceElements() {
        return Arrays.copyOf(stackTraceElements, stackTraceElements.length);
    }
}
