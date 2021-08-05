package net.serenitybdd.demos.todos.screenplay.tasks;

import net.serenitybdd.demos.todos.screenplay.user_interface.TodoList;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.SilentTask;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.conditions.Check;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isCurrentlyVisible;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;

public class DeleteAllTheItems {
    public static Performable onThePage() {
        return SilentTask.where(
                Check.whether(the(TodoList.TOGGLE_ALL_LABEL), isCurrentlyVisible()).andIfSo(
                        Click.on(TodoList.TOGGLE_ALL_LABEL),
                        Click.on(TodoList.CLEAR_COMPLETED)
                )
        );
    }
}
