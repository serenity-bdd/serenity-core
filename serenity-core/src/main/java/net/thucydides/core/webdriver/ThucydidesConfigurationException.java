package net.thucydides.core.webdriver;

/**
 * Created by john on 28/10/2014.
 */
public class ThucydidesConfigurationException extends RuntimeException {
    public ThucydidesConfigurationException(String s) {
        super(s);
    }

    public ThucydidesConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
