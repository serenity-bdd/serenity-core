package net.serenitybdd.demos.todos.screenplay.questions;

import net.serenitybdd.demos.todos.screenplay.model.TodoStatusFilter;
import net.serenitybdd.demos.todos.screenplay.user_interface.TodoList;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.Text;

public class CurrentFilter {

    public static Question<TodoStatusFilter> selected() {
        return Text.of(TodoList.SELECTED_FILTER)
                .describedAs("the current filter")
                .asEnum(TodoStatusFilter.class);
    }
}