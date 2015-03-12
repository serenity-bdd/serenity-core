package net.thucydides.core.webdriver.exceptions;

import org.openqa.selenium.TimeoutException;

/**
 * Created by john on 10/03/15.
 */

public class ElementShouldBePresentException extends TimeoutException {
    public ElementShouldBePresentException(String message, Throwable cause) {
        super(message, cause);
    }
}
