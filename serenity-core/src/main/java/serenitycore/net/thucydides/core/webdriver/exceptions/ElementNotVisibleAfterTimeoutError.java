package serenitycore.net.thucydides.core.webdriver.exceptions;

import serenitymodel.net.serenitybdd.core.exceptions.CausesAssertionFailure;

public class ElementNotVisibleAfterTimeoutError extends Error implements CausesAssertionFailure {
    public ElementNotVisibleAfterTimeoutError(String message) {
        super(message);
    }
}