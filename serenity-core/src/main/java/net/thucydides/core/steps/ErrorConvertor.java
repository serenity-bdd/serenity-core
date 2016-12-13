package net.thucydides.core.steps;

import net.serenitybdd.core.exceptions.SerenityManagedException;

import java.io.Serializable;

public class ErrorConvertor implements Serializable {

    private final Throwable throwable;
    public static ErrorConvertor forError(final Throwable throwable) {
        return new ErrorConvertor(throwable);
    }

    protected ErrorConvertor(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable convertToAssertion() {
        if (RuntimeException.class.isAssignableFrom(throwable.getClass())) {
            return throwable;
        } else {
            return new SerenityManagedException(throwable);
        }
    }
}