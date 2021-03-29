package net.serenitybdd.demos.todos.tasks;

import net.serenitybdd.demos.todos.pages.components.FilterBar;
import serenityscreenplay.screenplay.Actor;
import serenityscreenplay.screenplay.Performable;
import serenityscreenplay.screenplay.actions.Click;
import net.thucydides.core.annotations.Step;

public class ClearCompletedItems implements Performable {

    @Step("{0} clears all the completed items")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Click.on(FilterBar.CLEAR_COMPLETED));
    }
}
