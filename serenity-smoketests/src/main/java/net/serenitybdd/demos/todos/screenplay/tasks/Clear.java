package net.serenitybdd.demos.todos.screenplay.tasks;

import net.serenitybdd.demos.todos.screenplay.user_interface.TodoList;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;

public class Clear {
    public static Performable completedItems() {
        return Task.where("{0} clears all the completed items",
                Click.on(TodoList.CLEAR_COMPLETED)
        );
    }
}