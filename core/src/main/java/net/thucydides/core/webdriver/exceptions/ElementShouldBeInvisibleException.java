package net.thucydides.core.webdriver.exceptions;

import org.openqa.selenium.TimeoutException;

/**
 * Created by john on 10/03/15.
 */
public class ElementShouldBeInvisibleException extends TimeoutException {
    public ElementShouldBeInvisibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
