package net.serenitybdd.junit.runners.integration;

import net.serenitybdd.core.exceptions.UnrecognisedException;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.junit.runners.integration.exceptions.*;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SerenityRunner.class)
public class WhenRunningASerenityTestWithExceptions {

    @Steps
    MySteps mySteps;

    @Test
    public void shouldThrowDefaultConstructorExceptionInStep() {
        assertThrows(DefaultConstructorException.class, ()->mySteps.throwDefaultConstructorException());
    }

    @Test
    public void shouldThrowEmptyConstructorExceptionInStep() {
        assertThrows(EmptyConstructorException.class, ()->mySteps.throwEmptyConstructorException());
    }

    @Test
    public void shouldThrowStringConstructorExceptionInStep() {
        assertThrows(StringConstructorException.class, ()-> mySteps.throwStringConstructorException());
    }

    @Test
    public void shouldThrowStringThrowableConstructorExceptionInStep(){
        assertThrows(StringThrowableConstructorException.class, ()->mySteps.throwStringThrowableConstructorException());
    }

    @Test
    public void shouldThrowThrowableConstructorExceptionInStep() {
        assertThrows(ThrowableConstructorException.class, ()->mySteps.throwThrowableConstructorException());
    }

    @Test
    public void shouldThrowUnsupportedConstructorExceptionInStep() {
        assertThrows(UnrecognisedException.class, ()->mySteps.throwUnsupportedConstructorException());
    }

    @Test
    public void shouldThrowExceptionInTest() {
        assertThrows(DefaultConstructorException.class, ()-> {
            throw new DefaultConstructorException();
        });
    }

    @Test
    public void shouldAllowRecursiveParameters() {
        RecursivePojo parent = new RecursivePojo(null, "parent");
        RecursivePojo child = new RecursivePojo(parent, "child");
        parent.setParent(child);

        mySteps.stepWithRecursiveParameter(child);
    }


}
