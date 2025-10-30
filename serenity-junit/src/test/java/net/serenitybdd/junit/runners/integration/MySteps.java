package net.serenitybdd.junit.runners.integration;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.junit.runners.integration.exceptions.*;

import java.math.BigDecimal;

public class MySteps {

    @Step
    public void throwDefaultConstructorException() throws DefaultConstructorException {
        throw new DefaultConstructorException();
    }

    @Step
    public void throwEmptyConstructorException() throws EmptyConstructorException {
        throw new EmptyConstructorException();
    }

    @Step
    public void throwStringConstructorException() throws StringConstructorException {
        throw new StringConstructorException("Oh bother!");
    }

    @Step
    public void throwStringThrowableConstructorException() throws StringThrowableConstructorException {
        throw new StringThrowableConstructorException("Oh bother!", new AssertionError("Oh bother"));
    }

    @Step
    public void throwThrowableConstructorException() throws ThrowableConstructorException {
        throw new ThrowableConstructorException(new AssertionError("Oh bother"));
    }

    @Step
    public void throwUnsupportedConstructorException() throws UnsupportedConstructorException {
        throw new UnsupportedConstructorException(new BigDecimal("1.0"));
    }

    @Step
    public void stepWithRecursiveParameter(RecursivePojo recursivePojo) {

    }
}
