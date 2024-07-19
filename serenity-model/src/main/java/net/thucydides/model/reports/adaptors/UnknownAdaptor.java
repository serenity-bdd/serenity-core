package net.thucydides.model.reports.adaptors;

public class UnknownAdaptor extends RuntimeException {
    public UnknownAdaptor(String message) {
        super(message);
    }

    public UnknownAdaptor(String message, Throwable cause) {
        super(message, cause);
    }

}
