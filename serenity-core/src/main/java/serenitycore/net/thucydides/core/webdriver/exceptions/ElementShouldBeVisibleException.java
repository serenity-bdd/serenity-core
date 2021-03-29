package serenitycore.net.thucydides.core.webdriver.exceptions;

import serenitymodel.net.serenitybdd.core.exceptions.CausesAssertionFailure;
import org.openqa.selenium.ElementNotVisibleException;

public class ElementShouldBeVisibleException extends ElementNotVisibleException implements CausesAssertionFailure {
    public ElementShouldBeVisibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
