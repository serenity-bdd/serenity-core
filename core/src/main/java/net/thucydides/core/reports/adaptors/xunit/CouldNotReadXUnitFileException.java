package net.thucydides.core.reports.adaptors.xunit;

public class CouldNotReadXUnitFileException extends RuntimeException {
    public CouldNotReadXUnitFileException(String message) {
        super(message);
    }
}
