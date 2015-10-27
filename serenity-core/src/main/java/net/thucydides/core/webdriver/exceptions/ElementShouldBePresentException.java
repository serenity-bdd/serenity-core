package net.thucydides.core.webdriver.exceptions;

import org.openqa.selenium.TimeoutException;

public class ElementShouldBePresentException extends TimeoutException implements CausesAssertionFailure  {
    public ElementShouldBePresentException(String message, Throwable cause) {
        super(message, cause);
    }
}
