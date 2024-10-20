package net.serenitybdd.demos.todos.screenplay.tasks;

import net.serenitybdd.demos.todos.screenplay.user_interface.TodoListItem;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;

import java.util.Arrays;
import java.util.List;

public class CompleteItems {
    public static Performable called(String... itemNames) {
        return Task.where("{0} completes the items called: " + itemNames,
                actor -> {
                    Arrays.asList(itemNames).forEach(itemName -> actor.attemptsTo(
                            Click.on(TodoListItem.COMPLETE_ITEM.of(itemName))
                    ));
                }
        );
    }
}