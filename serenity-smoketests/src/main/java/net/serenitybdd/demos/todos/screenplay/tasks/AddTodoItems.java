package net.serenitybdd.demos.todos.screenplay.tasks;

import com.google.common.collect.ImmutableList;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.annotations.Step;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;

public class AddTodoItems implements Task {

    private List<String> todos;

    public AddTodoItems(){}
    public AddTodoItems(Collection<String> items) { this.todos = ImmutableList.copyOf(items); }

    @Step("{0} adds the todo items called: #todos")
    public <T extends Actor> void performAs(T actor) {
        todos.forEach(
                todo -> actor.attemptsTo(AddATodoItem.called(todo))
        );
    }

    public static AddTodoItems called(String... items) {
        return new AddTodoItems(asList(items));
    }

    public static AddTodoItems called(Collection<String> items) {
        return new AddTodoItems(items);
    }
}
