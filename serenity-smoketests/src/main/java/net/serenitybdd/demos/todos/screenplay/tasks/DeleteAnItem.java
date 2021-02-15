package net.serenitybdd.demos.todos.screenplay.tasks;

import net.serenitybdd.demos.todos.screenplay.actions.JSClick;
import net.serenitybdd.demos.todos.screenplay.user_interface.TodoListItem;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;

public class DeleteAnItem {
    public static Performable called(String itemName) {
        return Task.where("{0} deletes the item " + itemName,
                JSClick.on(TodoListItem.DELETE_ITEM.of(itemName))
        );
    }
}