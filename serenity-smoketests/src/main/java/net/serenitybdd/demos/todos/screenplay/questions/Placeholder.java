package net.serenitybdd.demos.todos.screenplay.questions;

import net.serenitybdd.demos.todos.screenplay.user_interface.TodoList;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.Attribute;

public class Placeholder {
    public static Question<String> text() {
        return Attribute.of(TodoList.WHAT_NEEDS_TO_BE_DONE)
                        .named("placeholder")
                        .describedAs("the placeholder text");
    }
}
