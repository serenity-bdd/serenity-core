package net.thucydides.model.reports;

/**
 * Report loading has failed for some reason.
 */
public class ReportLoadingFailedError extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ReportLoadingFailedError(final String message, final Throwable e) {
        super(message, e);
    }
}
