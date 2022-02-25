package net.serenitybdd.core.webdriver.driverproviders;

/**
 * Thrown if an error occurs during a call to a BeforeAWebdriverScenario class.
 */
public class WebDriverInitialisationException extends RuntimeException {
    public WebDriverInitialisationException(String message, Throwable e) {
        super(message,e);
    }
}
