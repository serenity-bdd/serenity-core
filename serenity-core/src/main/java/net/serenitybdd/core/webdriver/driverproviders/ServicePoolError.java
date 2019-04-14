package net.serenitybdd.core.webdriver.driverproviders;

public class ServicePoolError extends RuntimeException {

    public ServicePoolError(String message) {
        super(message);
    }
}
