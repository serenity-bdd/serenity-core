package net.serenitybdd.junit.runners.integration.exceptions;

import java.math.BigDecimal;

public class UnsupportedConstructorException extends Exception {
    public UnsupportedConstructorException(BigDecimal value) {
        super();
    }
}