package net.thucydides.core.webdriver.exceptions;

import net.serenitybdd.core.exceptions.CausesAssertionFailure;
import org.openqa.selenium.ElementNotInteractableException;

public class ElementShouldBeVisibleException extends ElementNotInteractableException implements CausesAssertionFailure {
    public ElementShouldBeVisibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
