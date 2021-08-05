package net.serenitybdd.demos.todos.screenplay.tasks;

import net.serenitybdd.demos.todos.screenplay.user_interface.TodoList;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Evaluate;

public class ToggleStatus {
    public static Performable ofAllItems() {
        return Task.where("{0} toggles the status of all items",
                Evaluate.javascript("arguments[0].click();", TodoList.TOGGLE_ALL_BUTTON)
        );
    }
}