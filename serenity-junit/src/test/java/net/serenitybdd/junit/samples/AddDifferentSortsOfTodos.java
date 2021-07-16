package net.serenitybdd.junit.samples;

import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Manual;
import net.thucydides.core.annotations.Steps;
import net.serenitybdd.junit.annotations.Concurrent;
import net.serenitybdd.junit.annotations.TestData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.Collection;

@RunWith(SerenityParameterizedRunner.class)
@Concurrent
public class AddDifferentSortsOfTodos {

    @Managed
    WebDriver janesBrowser;

    @TestData
    public static Collection<Object[]> todoItems(){
        return Arrays.asList(new Object[][]{
                {"walk the lion"},
                {"wash the dishes"},
                {"feed the ferrets"},
                {"count the rabbits"},
        });
    }

    @Steps
    AnnotatedSampleScenarioSteps jane;

    private final String todo;


    public AddDifferentSortsOfTodos(String todo) {
        this.todo = todo;
    }


    @Before
    public void openTheApplication() {
        jane.stepThatSucceeds();
    }

    @Test
    @Manual
    public void shouldBeAbleToAddANewTodoItem() {

        jane.stepThatSucceeds();
        jane.anotherStepThatSucceeds();
    }

}
