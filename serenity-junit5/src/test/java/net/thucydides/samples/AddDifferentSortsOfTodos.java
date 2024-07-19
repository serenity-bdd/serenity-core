package net.thucydides.samples;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.Manual;
import net.serenitybdd.annotations.Steps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.WebDriver;

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
