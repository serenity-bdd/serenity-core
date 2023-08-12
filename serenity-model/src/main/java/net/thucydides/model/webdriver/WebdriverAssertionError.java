package net.thucydides.model.webdriver;

/**
 * Turns a webdriver error into an ordinary assertion error.
 */
public class WebdriverAssertionError extends AssertionError {

    private static final long serialVersionUID = 1L;

    public WebdriverAssertionError(Throwable cause) {
        super(cause);
        this.setStackTrace(cause.getStackTrace());
    }
}
