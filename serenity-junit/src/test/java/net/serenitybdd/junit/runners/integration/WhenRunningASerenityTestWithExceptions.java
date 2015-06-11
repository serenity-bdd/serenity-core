package net.serenitybdd.junit.runners.integration;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class WhenRunningASerenityTestWithExceptions {

    @Steps
    MySteps mySteps;

    @Test(expected = MyException.class)
    public void shouldThrowExceptionInStep() throws MyException {
        mySteps.throwException();
    }

    @Test(expected = MyException.class)
    public void shouldThrowExceptionInTest() throws MyException {
        throw new MyException();
    }

}
