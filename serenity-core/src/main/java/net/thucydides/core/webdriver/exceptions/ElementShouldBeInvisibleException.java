package net.thucydides.core.webdriver.exceptions;

import net.serenitybdd.model.exceptions.CausesAssertionFailure;
import org.openqa.selenium.TimeoutException;

public class ElementShouldBeInvisibleException extends TimeoutException  implements CausesAssertionFailure {
    public ElementShouldBeInvisibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
