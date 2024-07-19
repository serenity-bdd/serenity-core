package net.thucydides.core.webdriver.exceptions;

import net.serenitybdd.model.exceptions.CausesAssertionFailure;
import org.openqa.selenium.TimeoutException;

public class ElementShouldBeDisabledException extends TimeoutException implements CausesAssertionFailure {
    public ElementShouldBeDisabledException(String message, Throwable cause) {
        super(message, cause);
    }
}
