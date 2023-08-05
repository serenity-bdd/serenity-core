package net.serenitybdd.junit.runners.integration;

import net.serenitybdd.model.exceptions.UnrecognisedException;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.junit.runners.integration.exceptions.*;
import net.serenitybdd.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class WhenRunningASerenityTestWithExceptions {

    @Steps
    MySteps mySteps;

    @Test(expected = DefaultConstructorException.class)
    public void shouldThrowDefaultConstructorExceptionInStep() throws DefaultConstructorException {
        mySteps.throwDefaultConstructorException();
    }

    @Test(expected = EmptyConstructorException.class)
    public void shouldThrowEmptyConstructorExceptionInStep() throws EmptyConstructorException {
        mySteps.throwEmptyConstructorException();
    }

    @Test(expected = StringConstructorException.class)
    public void shouldThrowStringConstructorExceptionInStep() throws StringConstructorException {
        mySteps.throwStringConstructorException();
    }

    @Test(expected = StringThrowableConstructorException.class)
    public void shouldThrowStringThrowableConstructorExceptionInStep() throws StringThrowableConstructorException {
        mySteps.throwStringThrowableConstructorException();
    }

    @Test(expected = ThrowableConstructorException.class)
    public void shouldThrowThrowableConstructorExceptionInStep() throws ThrowableConstructorException {
        mySteps.throwThrowableConstructorException();
    }

    @Test(expected = UnrecognisedException.class)
    public void shouldThrowUnsupportedConstructorExceptionInStep() throws UnsupportedConstructorException {
        mySteps.throwUnsupportedConstructorException();
    }

    @Test(expected = DefaultConstructorException.class)
    public void shouldThrowExceptionInTest() throws DefaultConstructorException {
        throw new DefaultConstructorException();
    }

    @Test
    public void shouldAllowRecursiveParameters() {
        RecursivePojo parent = new RecursivePojo(null, "parent");
        RecursivePojo child = new RecursivePojo(parent, "child");
        parent.setParent(child);

        mySteps.stepWithRecursiveParameter(child);
    }


}
