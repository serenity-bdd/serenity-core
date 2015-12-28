package net.serenitybdd.screenplay.exceptions;

public class UnexpectedEnumValueException extends AssertionError {

    public UnexpectedEnumValueException(String detailMessage) {
        super(detailMessage);
    }
}
