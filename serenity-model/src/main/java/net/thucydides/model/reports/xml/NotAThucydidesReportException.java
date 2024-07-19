package net.thucydides.model.reports.xml;

/**
 * Exception thrown if we attempt to process a file that is not a valid Thucydides XML report.
 * @author johnsmart
 *
 */
public class NotAThucydidesReportException extends Exception {

    private static final long serialVersionUID = 1L;

    public NotAThucydidesReportException(final String message, final Throwable e) {
        super(message, e);
    }

}
