package net.serenitybdd.demos.todos.screenplay.tasks;

import net.serenitybdd.demos.todos.screenplay.model.TodoStatusFilter;
import net.serenitybdd.demos.todos.screenplay.user_interface.TodoList;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;

public class FilterItems {
    public static Performable toShow(TodoStatusFilter filter) {
        return Task.where("{0} filters items by " + filter,
                Click.on(TodoList.FILTER.of(filter.name()))
        );
    }
}