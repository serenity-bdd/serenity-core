package net.serenitybdd.model.exceptions;

public class TheErrorType {

    private final String actualFailureCause;

    public TheErrorType(String actualFailureCause) {
        this.actualFailureCause = actualFailureCause;
    }

    public static TheErrorType causedBy(String actualFailureCause) {
        return new TheErrorType(actualFailureCause);
    }

    public boolean isAKindOf(Class<? extends Throwable> expected) {
        try {
            return expected.isAssignableFrom(Class.forName(actualFailureCause));
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
