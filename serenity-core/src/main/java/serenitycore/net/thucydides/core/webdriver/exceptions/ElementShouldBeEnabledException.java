package serenitycore.net.thucydides.core.webdriver.exceptions;

import serenitymodel.net.serenitybdd.core.exceptions.CausesAssertionFailure;
import org.openqa.selenium.TimeoutException;

public class ElementShouldBeEnabledException extends TimeoutException  implements CausesAssertionFailure {
    public ElementShouldBeEnabledException(String message) {
        super(message);
    }
    public ElementShouldBeEnabledException(String message, Throwable cause) {
        super(message, cause);
    }
}
