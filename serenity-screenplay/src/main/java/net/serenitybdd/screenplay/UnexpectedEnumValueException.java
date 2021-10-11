package net.serenitybdd.screenplay;

public class UnexpectedEnumValueException extends AssertionError {

    public UnexpectedEnumValueException(String detailMessage) {
        super(detailMessage);
    }
}
