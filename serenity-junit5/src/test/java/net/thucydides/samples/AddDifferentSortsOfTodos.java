package net.thucydides.samples;

import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Manual;
import net.thucydides.core.annotations.Steps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.Collection;

public class AddDifferentSortsOfTodos {

    @Managed
    WebDriver janesBrowser;
    
    @Steps
    AnnotatedSampleScenarioSteps jane;

    private final String todo;

    public AddDifferentSortsOfTodos(String todo) {
        this.todo = todo;
    }

    @BeforeEach
    public void openTheApplication() {
        jane.stepThatSucceeds();
    }
    
    @Manual
    @ParameterizedTest
    @ValueSource(strings = { "walk the lion","wash the dishes","feed the ferrets","count the rabbits" })
    public void shouldBeAbleToAddANewTodoItem() {

        jane.stepThatSucceeds();
        jane.anotherStepThatSucceeds();
    }

}
