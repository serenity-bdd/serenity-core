package net.serenitybdd.demos.todos.screenplay.tasks;

import com.google.common.base.Joiner;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Open;

import java.util.Arrays;
import java.util.List;

public class Start {

    public static Performable withAnEmptyTodoList() {
        return Task.where("{0} starts with an empty todo list",
                Open.browserOn().thePageNamed("home.page")
        );
    }

    public static Performable withATodoListContaining(String... items) {
        return withATodoListContaining(Arrays.asList(items));
    }

    public static Performable withATodoListContaining(List<String> items) {
        return Task.where("{0} starts with a todo list containing " + Joiner.on(", ").join(items),
                Open.browserOn().thePageNamed("home.page"),
                AddTodoItems.called(items)
        );
    }
}