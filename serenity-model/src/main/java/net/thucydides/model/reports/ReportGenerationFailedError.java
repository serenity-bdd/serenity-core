package net.thucydides.model.reports;

/**
 * Report generation has failed for some reason.
 */
public class ReportGenerationFailedError extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ReportGenerationFailedError(final String message, final Throwable e) {
        super(message, e);
    }
}
