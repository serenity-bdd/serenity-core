package net.thucydides.core.steps;


/**
 * Description and underlying cause behind a step failure.
 * A <code>StepFailure</code> holds a description of the failed test step and the
 * exception that was thrown while running it. In most cases the Description
 * will be of a single test step.
 */
public class StepFailure {

    private final ExecutedStepDescription description;

    private final Throwable cause;


    public StepFailure(final ExecutedStepDescription description, final Throwable cause) {
        this.description = description;
        this.cause = cause;
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
        return getException().getMessage();
    }
}
