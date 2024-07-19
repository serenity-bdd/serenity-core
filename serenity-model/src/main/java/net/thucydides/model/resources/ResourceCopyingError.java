package net.thucydides.model.resources;

/**
 * An error occurred when copying resources required for the HTML reports.
 */
public class ResourceCopyingError extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceCopyingError(final String message, final Throwable e) {
        super(message, e);
    }
}
