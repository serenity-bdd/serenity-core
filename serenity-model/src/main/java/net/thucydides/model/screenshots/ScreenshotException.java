package net.thucydides.model.screenshots;

/**
 * The screenshot could not be taken for some reason.
 */
public class ScreenshotException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ScreenshotException(final String message, final Throwable e) {
        super(message,e);
    }

    public ScreenshotException(final String message) {
        super(message);
    }

}
