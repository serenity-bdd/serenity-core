package net.thucydides.core.steps;

import net.serenitybdd.model.exceptions.SerenityManagedException;

public class ErrorConvertor {

    public static Throwable convertToAssertion(final Throwable throwable) {
        if (RuntimeException.class.isAssignableFrom(throwable.getClass())) {
            return throwable;
        } else if (AssertionError.class.isAssignableFrom(throwable.getClass())) {
            return throwable;
        } else {
            return new SerenityManagedException(throwable);
        }
    }
}
