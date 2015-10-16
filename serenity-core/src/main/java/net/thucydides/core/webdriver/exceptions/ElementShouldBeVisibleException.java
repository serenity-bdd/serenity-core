package net.thucydides.core.webdriver.exceptions;

import org.openqa.selenium.ElementNotVisibleException;

/**
 * Created by john on 10/03/15.
 */

public class ElementShouldBeVisibleException extends ElementNotVisibleException {
    public ElementShouldBeVisibleException(String message) {
        super(message);
    }

    public ElementShouldBeVisibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
