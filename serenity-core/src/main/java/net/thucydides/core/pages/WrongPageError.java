package net.thucydides.core.pages;

/**
 * We have navigated to the wrong page.
 *
 */
public class WrongPageError extends AssertionError {

    private static final long serialVersionUID = 1L;

    public WrongPageError(final String message) {
        super(message);
    }

}
