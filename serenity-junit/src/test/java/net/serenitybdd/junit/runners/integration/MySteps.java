package net.serenitybdd.junit.runners.integration;

import net.thucydides.core.annotations.Step;

public class MySteps {

        @Step
        public void throwException() throws MyException{
           throw new MyException();
        }
    }