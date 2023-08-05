package net.thucydides.core.webdriver.exceptions;

import net.serenitybdd.model.exceptions.CausesAssertionFailure;
import org.openqa.selenium.TimeoutException;

public class ElementShouldBeEnabledException extends TimeoutException  implements CausesAssertionFailure {
    public ElementShouldBeEnabledException(String message) {
        super(message);
    }
    public ElementShouldBeEnabledException(String message, Throwable cause) {
        super(message, cause);
    }
}
