package net.serenitybdd.core.webdriver.appium;

public class NoAvailableDeviceException extends RuntimeException {
    public NoAvailableDeviceException(String message) {
        super(message);
    }
}
