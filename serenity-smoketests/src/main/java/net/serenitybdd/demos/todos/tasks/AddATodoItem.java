package net.serenitybdd.demos.todos.tasks;

import net.serenitybdd.demos.todos.pages.components.ToDoList;
import serenityscreenplay.screenplay.Actor;
import serenityscreenplay.screenplay.Performable;
import serenityscreenplay.screenplay.actions.Enter;
import serenityscreenplay.screenplay.actions.Hit;
import net.thucydides.core.annotations.Step;

import static serenityscreenplay.screenplay.Tasks.instrumented;
import static org.openqa.selenium.Keys.RETURN;

public class AddATodoItem implements Performable {

    private final String thingToDo;

    @Step("{0} adds a todo item called #thingToDo")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Enter.theValue(thingToDo).into(ToDoList.NEW_TODO_FIELD),
                Hit.the(RETURN).keyIn(ToDoList.NEW_TODO_FIELD)
        );
    }

    public AddATodoItem(String thingToDo) {
        this.thingToDo = thingToDo;
    }

    public static AddATodoItem called(String thingToDo) {
        return instrumented(AddATodoItem.class, thingToDo);
    }

}
