package net.serenitybdd.core.webdriver.driverproviders;

public class RemoteDriverConfigurationError extends RuntimeException {
    public RemoteDriverConfigurationError(String message) {
        super(message);
    }

    public RemoteDriverConfigurationError(String message, Throwable e) {
        super(message, e);
    }
}
